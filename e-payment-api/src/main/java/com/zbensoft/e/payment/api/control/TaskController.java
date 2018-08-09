package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.TaskService;
import com.zbensoft.e.payment.api.service.api.TaskTypeService;
import com.zbensoft.e.payment.db.domain.Task;
import com.zbensoft.e.payment.db.domain.TaskType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/task")
@RestController
public class TaskController {
	@Autowired
	TaskService taskService;
	@Autowired
	TaskTypeService taskTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@ApiOperation(value = "Query task,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<Task>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String taskTypeId,
			@RequestParam(required = false) String name, @RequestParam(required = false) String cronExpression,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String remark ,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		Task task = new Task();
		task.setTaskId(id);
		task.setTaskTypeId(taskTypeId);
		task.setName(name);
		task.setCronExpression(cronExpression);
		task.setStatus(status);
		task.setRemark(remark);

		int count = taskService.count(task);
		if (count == 0) {
			return new ResponseRestEntity<List<Task>>(new ArrayList<Task>(), HttpRestStatus.NOT_FOUND);
		}

		List<Task> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = taskService.selectPage(task);
		} else {
			list = taskService.selectPage(task);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<Task>>(new ArrayList<Task>(), HttpRestStatus.NOT_FOUND);
		}
	
		List<Task> listNew = new ArrayList<Task>();
		for(Task bean:list){
			TaskType type = taskTypeService.selectByPrimaryKey(bean.getTaskTypeId());
			if(type!=null){
				bean.setName(type.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<Task>>(listNew, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query Task", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<Task> selectByPrimaryKey(@PathVariable("id") String id) {
		Task Task = taskService.selectByPrimaryKey(id);
		if (Task == null) {
			return new ResponseRestEntity<Task>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<Task>(Task, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add Task", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtask(@RequestBody Task task,BindingResult result,  UriComponentsBuilder ucBuilder) {
		task.setTaskId(IDGenerate.generateCommTwo(IDGenerate.TASK));
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
		if (taskService.isExist(task)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		taskService.insert(task);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, task,CommonLogImpl.TASK);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/task/{id}").buildAndExpand(task.getTaskId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit Task", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<Task> updatetask(@PathVariable("id") String id, @RequestBody Task task) {

		Task type = taskService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<Task>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setTaskId(task.getTaskId());
		type.setTaskTypeId(task.getTaskTypeId());
		type.setName(task.getName());
		type.setCronExpression(task.getCronExpression());
		type.setStatus(task.getStatus());
		type.setRemark(task.getRemark());
		taskService.updateByPrimaryKey(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.TASK);
		return new ResponseRestEntity<Task>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part Task", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<Task> updatetaskSelective(@PathVariable("id") String id, @RequestBody Task task) {

		Task type = taskService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<Task>(HttpRestStatus.NOT_FOUND);
		}
		taskService.updateByPrimaryKeySelective(task);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, task,CommonLogImpl.TASK);
		return new ResponseRestEntity<Task>(type, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete Task", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<Task> deletetask(@PathVariable("id") String id) {

		Task task = taskService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<Task>(HttpRestStatus.NOT_FOUND);
		}

		taskService.deleteByPrimaryKey(id);
		//删除日志开始
		Task delBean = new Task();
		delBean.setTaskId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.TASK);
		//删除日志结束
		return new ResponseRestEntity<Task>(HttpRestStatus.NO_CONTENT);
	}

	// 用户启用
		@ApiOperation(value = "enable the specified task", notes = "")
		@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<Task> enableTask(@PathVariable("id") String id) {

			Task task = taskService.selectByPrimaryKey(id);
			if (task == null) {
				return new ResponseRestEntity<Task>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(0);
			taskService.updateByPrimaryKey(task);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, task,CommonLogImpl.TASK);
			return new ResponseRestEntity<Task>(HttpRestStatus.OK);
		}
		
		// 用户停用
		@ApiOperation(value = "enable the specified task", notes = "")
		@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<Task> disableSysUser(@PathVariable("id") String id) {

			Task task = taskService.selectByPrimaryKey(id);
			if (task == null) {
				return new ResponseRestEntity<Task>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(1);
			taskService.updateByPrimaryKey(task);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, task,CommonLogImpl.TASK);
			return new ResponseRestEntity<Task>(HttpRestStatus.OK);
		}
}