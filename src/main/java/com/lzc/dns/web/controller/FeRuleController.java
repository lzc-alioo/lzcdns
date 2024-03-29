package com.lzc.dns.web.controller;

import com.lzc.dns.manager.RuleManager;
import com.lzc.dns.util.IPUtils;
import com.lzc.dns.web.entity.Result;
import com.lzc.dns.web.entity.Rule;
import com.lzc.dns.web.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by matrixy on 2019/5/1.
 */
@Controller
@RequestMapping("/fe/manage/rule")
public class FeRuleController extends BaseController {
    @Autowired
    RuleService ruleService;


    @RequestMapping("/list")
    @ResponseBody
    public Result queryList(@RequestParam(required = false) String name, @RequestParam(defaultValue = "1") int pageIndex) {
        Result result = new Result();
        try {
            result.setData(ruleService.find(pageIndex, 20));
        } catch (Exception ex) {
            result.setError(ex);
        }
        return result;
    }

    // 添加新解析规则
    // ipFrom ipTo timeFrom timeTo matchMode name priority enabled dispatchMode
    @RequestMapping("/create")
    @ResponseBody
    @Transactional
    public Result createRule(@RequestParam String ipFrom,
                             @RequestParam String ipTo,
                             @RequestParam String timeFrom,
                             @RequestParam String timeTo,
                             @RequestParam String matchMode,
                             @RequestParam String name,
                             @RequestParam String dispatchMode,
                             @RequestParam String addresses) {
        Result result = new Result();
        try {
            Rule rule = new Rule();
            if (!StringUtils.isEmpty(ipFrom)) {
                if (ipFrom.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                    throw new RuntimeException("请输入正确的IP开始地址");
                rule.setIpFrom(IPUtils.toInteger(ipFrom));
            }
            if (!StringUtils.isEmpty(ipTo)) {
                if (ipTo.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                    throw new RuntimeException("请输入正确的IP结束地址");
                rule.setIpTo(IPUtils.toInteger(ipTo));
            }

            if (!StringUtils.isEmpty(timeFrom)) {
                if (timeFrom.matches("^\\d{2}:\\d{2}:\\d{2}$"))
                    rule.setTimeFrom(Integer.parseInt(timeFrom.replace(":", "")));
            }
            if (!StringUtils.isEmpty(timeTo)) {
                if (timeTo.matches("^\\d{2}:\\d{2}:\\d{2}$"))
                    rule.setTimeTo(Integer.parseInt(timeTo.replace(":", "")));
            }
            if (rule.getTimeFrom() == null || rule.getTimeTo() == null) {
                rule.setTimeFrom(null);
                rule.setTimeTo(null);
            }
            String[] addr = addresses.split("\r\n");

            if (StringUtils.isEmpty(matchMode)) matchMode = "contains";
            if (!matchMode.matches("^(suffix)|(prefix)|(contains)|(equals)$"))
                throw new RuntimeException("请选择匹配模式");
            if (StringUtils.isEmpty(name)) throw new RuntimeException("请输入要匹配解析的域名");
            if (StringUtils.isEmpty(dispatchMode)) dispatchMode = "round-robin";
            if (!dispatchMode.matches("^(round-robin)|(iphash)|(random)$"))
                throw new RuntimeException("请选择应答IP的分发模式");
            if (addr == null || addr.length == 0) throw new RuntimeException("请至少添加一个IP地址");

            int addressCount = 0;
            for (int i = 0; i < addr.length; i++) {
                if (StringUtils.isEmpty(addr[i])) continue;
                if (addr[i].matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                    throw new RuntimeException("请输入正确格式的IP应答地址");
                addressCount += 1;
            }
            if (addressCount == 0) throw new RuntimeException("请至少输入一个IP应答地址");


            rule.setPriority(0);
            rule.setMatchMode(matchMode);
            rule.setName(name);
            rule.setEnabled(true);
            rule.setDispatchMode(dispatchMode);
            rule.setAddresses(addresses);

            ruleService.create(rule);

            // 实时更新内存缓存中的规则列表
            RuleManager.getInstance().add(rule);
        } catch (Exception ex) {
            result.setError(ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    // 获取解析规则的详细信息，用于修改
    @RequestMapping("/query")
    @ResponseBody
    public Result query(@RequestParam Long ruleId) {
        Result result = new Result();
        try {
            Rule rule = ruleService.getById(ruleId);
            if (rule == null) throw new RuntimeException("查无此解析规则");

//            rule.setAddresses(addrService.find(ruleId));
            result.setData(rule);
        } catch (Exception ex) {
            result.setError(ex);
        }
        return result;
    }

    // 修改解析规则，含增删IP条目
    @RequestMapping("/update")
    @ResponseBody
    @Transactional
    public Result updateRule(@RequestParam Long ruleId,
                             @RequestParam String ipFrom,
                             @RequestParam String ipTo,
                             @RequestParam String timeFrom,
                             @RequestParam String timeTo,
                             @RequestParam String matchMode,
                             @RequestParam String name,
                             @RequestParam String dispatchMode,
                             @RequestParam String addresses) {
        Result result = new Result();

        Rule entity = null;
        try {
            entity = ruleService.getById(ruleId);
            if (entity == null) throw new RuntimeException("查无此解析规则");
            RuleManager.getInstance().remove(entity);

            Rule rule = new Rule();
            rule.setId(ruleId);

            if (!StringUtils.isEmpty(ipFrom)) {
                if (ipFrom.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                    throw new RuntimeException("请输入正确的IP开始地址");
                rule.setIpFrom(IPUtils.toInteger(ipFrom));
            }
            if (!StringUtils.isEmpty(ipTo)) {
                if (ipTo.matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                    throw new RuntimeException("请输入正确的IP结束地址");
                rule.setIpTo(IPUtils.toInteger(ipTo));
            }

            if (!StringUtils.isEmpty(timeFrom)) {
                if (timeFrom.matches("^\\d{2}:\\d{2}:\\d{2}$"))
                    rule.setTimeFrom(Integer.parseInt(timeFrom.replace(":", "")));
            }
            if (!StringUtils.isEmpty(timeTo)) {
                if (timeTo.matches("^\\d{2}:\\d{2}:\\d{2}$"))
                    rule.setTimeTo(Integer.parseInt(timeTo.replace(":", "")));
            }
            if (rule.getTimeFrom() == null || rule.getTimeTo() == null) {
                rule.setTimeFrom(null);
                rule.setTimeTo(null);
            }
            String[] addr = addresses.split("\r\n");

            if (StringUtils.isEmpty(matchMode)) matchMode = "contains";
            if (!matchMode.matches("^(suffix)|(prefix)|(contains)|(equals)$"))
                throw new RuntimeException("请选择匹配模式");
            if (StringUtils.isEmpty(name)) throw new RuntimeException("请输入要匹配解析的域名");
            if (StringUtils.isEmpty(dispatchMode)) dispatchMode = "round-robin";
            if (!dispatchMode.matches("^(round-robin)|(iphash)|(random)$"))
                throw new RuntimeException("请选择应答IP的分发模式");
            if (addr == null || addr.length == 0) throw new RuntimeException("请至少添加一个IP地址");

            int addressCount = 0;
            for (int i = 0; i < addr.length; i++) {
                if (StringUtils.isEmpty(addr[i])) continue;
                if (addr[i].matches("^(\\d{1,3})(\\.\\d{1,3}){3}$") == false)
                    throw new RuntimeException("请输入正确格式的IP应答地址");
                addressCount += 1;
            }
            if (addressCount == 0) throw new RuntimeException("请至少输入一个IP应答地址");


            rule.setPriority(0);
            rule.setMatchMode(matchMode);
            rule.setName(name);
            rule.setEnabled(true);
            rule.setDispatchMode(dispatchMode);
            rule.setAddresses(addresses);
            ruleService.update(rule);

            // 实时更新内存缓存中的规则列表
            RuleManager.getInstance().add(rule);
        } catch (Exception ex) {
            if (entity != null) RuleManager.getInstance().add(entity);
            result.setError(ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    // 禁用/启用解析规则
    @RequestMapping("/setEnable")
    @ResponseBody
    public Result setEnable(@RequestParam Long ruleId, @RequestParam Boolean enabled) {
        Result result = new Result();
        try {
            Rule rule = ruleService.getById(ruleId);
            if (rule == null) throw new RuntimeException("查无此解析规则");

            rule.setEnabled(enabled);
            ruleService.update(rule);

            if (enabled) RuleManager.getInstance().enable(ruleId);
            else RuleManager.getInstance().disable(ruleId);
        } catch (Exception ex) {
            result.setError(ex);
        }
        return result;
    }

    // 删除
    @RequestMapping("/remove")
    @ResponseBody
    @Transactional
    public Result remove(@RequestParam Long ruleId) {
        Result result = new Result();
        try {
            Rule rule = ruleService.getById(ruleId);
            if (null == rule) throw new RuntimeException("查无此解析规则");
            ruleService.remove(rule);
            RuleManager.getInstance().remove(rule);
        } catch (Exception ex) {
            result.setError(ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }
}
