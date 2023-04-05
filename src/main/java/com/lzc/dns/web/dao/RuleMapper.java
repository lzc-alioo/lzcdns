package com.lzc.dns.web.dao;

import com.lzc.dns.web.entity.Rule;
import com.lzc.dns.web.entity.RuleExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RuleMapper {
    long countByExample(RuleExample example);

    int deleteByExample(RuleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Rule record);

    int insertSelective(Rule record);

    List<Rule> selectByExample(RuleExample example);

    Rule selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Rule record, @Param("example") RuleExample example);

    int updateByExample(@Param("record") Rule record, @Param("example") RuleExample example);

    int updateByPrimaryKeySelective(Rule record);

    int updateByPrimaryKey(Rule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dns-ali..rule
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int batchInsert(@Param("list") List<Rule> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dns-ali..rule
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int batchInsertSelective(@Param("list") List<Rule> list, @Param("selective") Rule.Column... selective);
}