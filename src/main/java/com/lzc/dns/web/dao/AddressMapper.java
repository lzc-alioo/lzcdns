//package com.lzc.dns.web.dao;
//
//import com.lzc.dns.web.entity.Address;
//import com.lzc.dns.web.entity.AddressExample;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//
//@Mapper
//public interface AddressMapper {
//    long countByExample(AddressExample example);
//
//    int deleteByExample(AddressExample example);
//
//    int deleteByPrimaryKey(Long id);
//
//    int insert(Address record);
//
//    int insertSelective(Address record);
//
//    List<Address> selectByExample(AddressExample example);
//
//    Address selectByPrimaryKey(Long id);
//
//    int updateByExampleSelective(@Param("record") Address record, @Param("example") AddressExample example);
//
//    int updateByExample(@Param("record") Address record, @Param("example") AddressExample example);
//
//    int updateByPrimaryKeySelective(Address record);
//
//    int updateByPrimaryKey(Address record);
//
//    /**
//     * This method was generated by MyBatis Generator.
//     * This method corresponds to the database table dns-ali..address
//     *
//     * @mbg.generated
//     * @project https://github.com/itfsw/mybatis-generator-plugin
//     */
//    int batchInsert(@Param("list") List<Address> list);
//
//    /**
//     * This method was generated by MyBatis Generator.
//     * This method corresponds to the database table dns-ali..address
//     *
//     * @mbg.generated
//     * @project https://github.com/itfsw/mybatis-generator-plugin
//     */
//    int batchInsertSelective(@Param("list") List<Address> list, @Param("selective") Address.Column... selective);
//}