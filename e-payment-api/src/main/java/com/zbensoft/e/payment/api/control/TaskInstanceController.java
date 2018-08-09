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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.TaskInstanceService;
import com.zbensoft.e.payment.api.service.api.TaskService;
import com.zbensoft.e.payment.db.domain.Task;
import com.zbensoft.e.payment.db.domain.TaskInstance;
import com.zbensoft.e.payment.db.domain.TaskType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/taskInstance")
@RestController
public class TaskInstanceController {
	@Autowired
	TaskInstanceService taskInstanceService;
	@Autowired
	TaskService taskService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	@ApiOperation(value = "Query TaskInstance,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<TaskInstance>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String taskId,@RequestParam(required = false) Integer status,
			@RequestParam(required = false) Integer progress,
			@RequestParam(required = false) String message,
		    @RequestParam(required = false) String remark ,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		TaskInstance taskInstance = new TaskInstance();
		taskInstance.setTaskInstanceId(id);
		taskInstance.setTaskId(taskId);
		taskInstance.setStatus(status);
		taskInstance.setProgress(progress);
		taskInstance.setMessage(message);
		taskInstance.setRemark(remark);

		int count = taskInstanceService.count(taskInstance);
		if (count == 0) {
			return new ResponseRestEntity<List<TaskInstance>>(new ArrayList<TaskInstance>(), HttpRestStatus.NOT_FOUND);
		}

		List<TaskInstance> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = taskInstanceService.selectPage(taskInstance);
		} else {
			list = taskInstanceService.selectPage(taskInstance);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<TaskInstance>>(new ArrayList<TaskInstance>(), HttpRestStatus.NOT_FOUND);
		}
	
		List<TaskInstance> listNew = new ArrayList<TaskInstance>();
		for(TaskInstance bean:list){
			Task task = taskService.selectByPrimaryKey(bean.getTaskId());
			if(task!=null){
				bean.setName(task.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<TaskInstance>>(listNew, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query TaskInstance", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<TaskInstance> selectByPrimaryKey(@PathVariable("id") String id) {
		TaskInstance taskInstance = taskInstanceService.selectByPrimaryKey(id);
		if (taskInstance == null) {
			return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<TaskInstance>(taskInstance, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add TaskInstance", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtaskInstance(@RequestBody TaskInstance taskInstance,BindingResult result,  UriComponentsBuilder ucBuilder) {
		//taskInstance.setTaskInstanceId(System.currentTimeMillis() + "");
		taskInstance.setTaskInstanceId(IDGenerate.generateCommTwo(IDGenerate.TASK_INSTANCE));
		taskInstanceService.insert(taskInstance);
		//新增日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, taskInstance,CommonLogImpl.TASK);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/taskInstance/{id}").buildAndExpand(taskInstance.getTaskInstanceId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit TaskInstance", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<TaskInstance> updatetask(@PathVariable("id") String id, @RequestBody TaskInstance taskInstance) {

		TaskInstance type = taskInstanceService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setTaskInstanceId(taskInstance.getTaskInstanceId());
		type.setTaskId(taskInstance.getTaskId());
		type.setStatus(taskInstance.getStatus());
		type.setProgress(taskInstance.getProgress());
		type.setMessage(taskInstance.getMessage());
		type.setRemark(taskInstance.getRemark());
		taskInstanceService.updateByPrimaryKey(type);
		//修改日志
       CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.TASK);
		return new ResponseRestEntity<TaskInstance>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part TaskInstance", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<TaskInstance> updatetaskSelective(@PathVariable("id") String id, @RequestBody TaskInstance taskInstance) {

		TaskInstance type = taskInstanceService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NOT_FOUND);
		}
		taskInstanceService.updateByPrimaryKeySelective(taskInstance);
		//修改日志
	    CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, taskInstance,CommonLogImpl.TASK);
		return new ResponseRestEntity<TaskInstance>(type, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete TaskInstance", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<TaskInstance> deletetaskInstance(@PathVariable("id") String id) {

		TaskInstance task = taskInstanceService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NOT_FOUND);
		}

		taskInstanceService.deleteByPrimaryKey(id);
		//删除日志开始
		TaskInstance delBean = new TaskInstance();
		delBean.setTaskInstanceId(id);              

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.TASK);
		//删除日志结束
		return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NO_CONTENT);
	}

	// 未执行
			@ApiOperation(value = "enable the specified task", notes = "")
			@RequestMapping(value = "/unexecuted/{id}", method = RequestMethod.PUT)
			public ResponseRestEntity<TaskInstance> unexecuted(@PathVariable("id") String id) {

				TaskInstance task = taskInstanceService.selectByPrimaryKey(id);
				if (task == null) {
					return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NOT_FOUND);
				}
				//改变用户状态 0:未执行  1:执行  3:成功 4:失败
				task.setStatus(0);
				taskInstanceService.updateByPrimaryKey(task);
				//修改日志
			    CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UNIMPLEMENT, task,CommonLogImpl.TASK);
				return new ResponseRestEntity<TaskInstance>(HttpRestStatus.OK);
			}
			
			// 执行中
			@ApiOperation(value = "enable the specified sysUser", notes = "")
			@RequestMapping(value = "/executed/{id}", method = RequestMethod.PUT)
			public ResponseRestEntity<TaskInstance> executed(@PathVariable("id") String id) {

				TaskInstance task = taskInstanceService.selectByPrimaryKey(id);
				if (task == null) {
					return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NOT_FOUND);
				}
				//改变用户状态 0:未执行  1:执行  3:成功 4:失败
				task.setStatus(1);
				taskInstanceService.updateByPrimaryKey(task);
				//修改日志
			    CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_IMPLEMENT, task,CommonLogImpl.TASK);
				return new ResponseRestEntity<TaskInstance>(HttpRestStatus.OK);
			}
	
			//成功
			@ApiOperation(value = "enable the specified sysUser", notes = "")
			@RequestMapping(value = "/success/{id}", method = RequestMethod.PUT)
			public ResponseRestEntity<TaskInstance> success(@PathVariable("id") String id) {

				TaskInstance task = taskInstanceService.selectByPrimaryKey(id);
				if (task == null) {
					return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NOT_FOUND);
				}
				//改变用户状态 0:未执行  1:执行  3:成功 4:失败
				task.setStatus(3);
				taskInstanceService.updateByPrimaryKey(task);
				//修改日志
			    CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_SUCCESS, task,CommonLogImpl.TASK);
				return new ResponseRestEntity<TaskInstance>(HttpRestStatus.OK);
			}
			//失败
			@ApiOperation(value = "enable the specified sysUser", notes = "")
			@RequestMapping(value = "/failure/{id}", method = RequestMethod.PUT)
			public ResponseRestEntity<TaskInstance> failure(@PathVariable("id") String id) {

				TaskInstance task = taskInstanceService.selectByPrimaryKey(id);
				if (task == null) {
					return new ResponseRestEntity<TaskInstance>(HttpRestStatus.NOT_FOUND);
				}
				//改变用户状态 0:未执行  1:执行  3:成功 4:失败
				task.setStatus(4);
				taskInstanceService.updateByPrimaryKey(task);
				//修改日志
			    CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_FAIL, task,CommonLogImpl.TASK);
				return new ResponseRestEntity<TaskInstance>(HttpRestStatus.OK);
			}
}