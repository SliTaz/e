package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.Task;
import com.zbensoft.e.payment.db.domain.TaskInstance;

public interface TaskInstanceService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_instance
     *
     * @mbg.generated Mon Jun 19 16:16:12 CST 2017
     */
    int deleteByPrimaryKey(String taskInstanceId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_instance
     *
     * @mbg.generated Mon Jun 19 16:16:12 CST 2017
     */
    int insert(TaskInstance record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_instance
     *
     * @mbg.generated Mon Jun 19 16:16:12 CST 2017
     */
    int insertSelective(TaskInstance record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_instance
     *
     * @mbg.generated Mon Jun 19 16:16:12 CST 2017
     */
    TaskInstance selectByPrimaryKey(String taskInstanceId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_instance
     *
     * @mbg.generated Mon Jun 19 16:16:12 CST 2017
     */
    int updateByPrimaryKeySelective(TaskInstance record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_instance
     *
     * @mbg.generated Mon Jun 19 16:16:12 CST 2017
     */
    int updateByPrimaryKey(TaskInstance record);
    int deleteAll();
   	int count(TaskInstance instance);
   	List<TaskInstance> selectPage(TaskInstance taskInstance);
}
