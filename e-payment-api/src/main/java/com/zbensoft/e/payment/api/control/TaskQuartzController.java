package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.quartz.TaskQuartzInfo;
import com.zbensoft.e.payment.api.quartz.TaskQuartzServiceImpl;

import io.swagger.annotations.ApiOperation;

/**
 * 任务管理
 * 
 * @author lance
 */
@RequestMapping(value = "/taskq")
@RestController
public class TaskQuartzController {
	@Autowired
	private TaskQuartzServiceImpl taskQuartzServiceImpl;

	@PreAuthorize("hasRole('R_TASK_Q')")
	@ApiOperation(value = "Query taskq，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<TaskQuartzInfo>> list() {
		int count = 0;
		List<TaskQuartzInfo> infos = taskQuartzServiceImpl.list();

		if (infos == null || infos.isEmpty()) {
			return new ResponseRestEntity<List<TaskQuartzInfo>>(new ArrayList<TaskQuartzInfo>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<TaskQuartzInfo>>(infos, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_TASK_E')")
	@ApiOperation(value = "Add taskq", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> save(@Valid @RequestBody TaskQuartzInfo info) {
		HttpRestStatus httpRestStatus = taskQuartzServiceImpl.addJob(info);
		if(httpRestStatus== HttpRestStatus.OK){
			//新增日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, info,CommonLogImpl.TASK);
		}
		
		return new ResponseRestEntity<Void>(httpRestStatus);
	}

	@PreAuthorize("hasRole('R_TASK_E')")
	@ApiOperation(value = "Edit taskq", notes = "")
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public ResponseRestEntity<Void> update(@Valid @RequestBody TaskQuartzInfo info) {
		HttpRestStatus httpRestStatus = taskQuartzServiceImpl.edit(info);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, info,CommonLogImpl.TASK);
		return new ResponseRestEntity<Void>(httpRestStatus);
	}

	@PreAuthorize("hasRole('R_TASK_E')")
	@ApiOperation(value = "Delete taskq", notes = "")
	@RequestMapping(value = "/delete/{jobName}/{jobGroup}", method = RequestMethod.DELETE)
	public ResponseRestEntity<Void> delete(@PathVariable String jobName, @PathVariable String jobGroup) {
		HttpRestStatus httpRestStatus = taskQuartzServiceImpl.delete(jobName, jobGroup);
		//删除日志开始
				TaskQuartzInfo delBean = new TaskQuartzInfo();
				delBean.setJobName(jobName);             
				delBean.setJobGroup(jobGroup);         
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.TASK);
		return new ResponseRestEntity<Void>(httpRestStatus);
	}

	@PreAuthorize("hasRole('R_TASK_E')")
	@ApiOperation(value = "pause taskq", notes = "")
	@RequestMapping(value = "/pause/{jobName}/{jobGroup}", method = RequestMethod.GET)
	public ResponseRestEntity<Void> pause(@PathVariable String jobName, @PathVariable String jobGroup) {
		HttpRestStatus httpRestStatus = taskQuartzServiceImpl.pause(jobName, jobGroup);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_PAUSED, httpRestStatus,CommonLogImpl.TASK);
		return new ResponseRestEntity<Void>(httpRestStatus);
	}

	@PreAuthorize("hasRole('R_TASK_E')")
	@ApiOperation(value = "pause taskq", notes = "")
	@RequestMapping(value = "/resume/{jobName}/{jobGroup}", method = RequestMethod.GET)
	public ResponseRestEntity<Void> resume(@PathVariable String jobName, @PathVariable String jobGroup) {
		HttpRestStatus httpRestStatus = taskQuartzServiceImpl.resume(jobName, jobGroup);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_NORMAL, httpRestStatus,CommonLogImpl.TASK);
		return new ResponseRestEntity<Void>(httpRestStatus);
	}
}
