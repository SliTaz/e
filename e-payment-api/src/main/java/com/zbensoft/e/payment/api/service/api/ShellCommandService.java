package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ShellCommand;

public interface ShellCommandService {
	 /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shell_command
     *
     * @mbg.generated Tue Aug 29 11:10:53 CST 2017
     */
    int deleteByPrimaryKey(String shellCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shell_command
     *
     * @mbg.generated Tue Aug 29 11:10:53 CST 2017
     */
    int insert(ShellCommand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shell_command
     *
     * @mbg.generated Tue Aug 29 11:10:53 CST 2017
     */
    int insertSelective(ShellCommand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shell_command
     *
     * @mbg.generated Tue Aug 29 11:10:53 CST 2017
     */
    ShellCommand selectByPrimaryKey(String shellCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shell_command
     *
     * @mbg.generated Tue Aug 29 11:10:53 CST 2017
     */
    int updateByPrimaryKeySelective(ShellCommand record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table shell_command
     *
     * @mbg.generated Tue Aug 29 11:10:53 CST 2017
     */
    int updateByPrimaryKey(ShellCommand record);
    int count(ShellCommand record);
    ShellCommand selectByName(String name);
	List<ShellCommand> selectPage(ShellCommand record);
	boolean isExist(ShellCommand record);
}
