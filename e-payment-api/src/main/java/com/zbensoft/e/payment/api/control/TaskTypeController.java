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
import com.zbensoft.e.payment.api.service.api.TaskTypeService;
import com.zbensoft.e.payment.db.domain.Task;
import com.zbensoft.e.payment.db.domain.TaskType;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/taskType")
@RestController
public class TaskTypeController {
	@Autowired
	TaskTypeService taskTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@ApiOperation(value = "Query taskType,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<TaskType>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name, @RequestParam(required = false) String handleClass,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String remark ,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		TaskType taskType = new TaskType();
		taskType.setTaskTypeId(id);
		taskType.setName(name);
		taskType.setHandleClass(handleClass);
		taskType.setStatus(status);
		taskType.setRemark(remark);

		int count = taskTypeService.count(taskType);
		if (count == 0) {
			return new ResponseRestEntity<List<TaskType>>(new ArrayList<TaskType>(), HttpRestStatus.NOT_FOUND);
		}

		List<TaskType> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = taskTypeService.selectPage(taskType);
		} else {
			list = taskTypeService.selectPage(taskType);
		}

		return new ResponseRestEntity<List<TaskType>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query TaskType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<TaskType> selectByPrimaryKey(@PathVariable("id") String id) {
		TaskType TaskType = taskTypeService.selectByPrimaryKey(id);
		if (TaskType == null) {
			return new ResponseRestEntity<TaskType>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<TaskType>(TaskType, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add TaskType", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtaskType(@RequestBody TaskType taskType,BindingResult result,  UriComponentsBuilder ucBuilder) {
		taskType.setTaskTypeId(IDGenerate.generateCommTwo(IDGenerate.TASK_TYPE));
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
		if (taskTypeService.isExist(taskType)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		taskTypeService.insert(taskType);
		//新增日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, taskType,CommonLogImpl.TASK);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/taskType/{id}").buildAndExpand(taskType.getTaskTypeId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit TaskType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<TaskType> updatetaskType(@PathVariable("id") String id, @RequestBody TaskType taskType) {

		TaskType type = taskTypeService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<TaskType>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setTaskTypeId(taskType.getTaskTypeId());
		type.setName(taskType.getName());
		type.setHandleClass(taskType.getHandleClass());
		type.setStatus(taskType.getStatus());
		type.setRemark(taskType.getRemark());
		taskTypeService.updateByPrimaryKey(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.TASK);
		return new ResponseRestEntity<TaskType>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part TaskType", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<TaskType> updatetaskTypeSelective(@PathVariable("id") String id, @RequestBody TaskType taskType) {

		TaskType type = taskTypeService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<TaskType>(HttpRestStatus.NOT_FOUND);
		}
		taskTypeService.updateByPrimaryKeySelective(taskType);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, taskType,CommonLogImpl.TASK);
		return new ResponseRestEntity<TaskType>(type, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete TaskType", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<TaskType> deletetaskType(@PathVariable("id") String id) {

		TaskType taskType = taskTypeService.selectByPrimaryKey(id);
		if (taskType == null) {
			return new ResponseRestEntity<TaskType>(HttpRestStatus.NOT_FOUND);
		}

		taskTypeService.deleteByPrimaryKey(id);
		//删除日志开始
		TaskType delBean = new TaskType();
		delBean.setTaskTypeId(id);              

		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.TASK);
		//删除日志结束
		return new ResponseRestEntity<TaskType>(HttpRestStatus.NO_CONTENT);
	}
	// 用户启用
			@ApiOperation(value = "enable the specified TaskType", notes = "")
			@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
			public ResponseRestEntity<TaskType> enableTask(@PathVariable("id") String id) {

				TaskType task = taskTypeService.selectByPrimaryKey(id);
				if (task == null) {
					return new ResponseRestEntity<TaskType>(HttpRestStatus.NOT_FOUND);
				}
				//改变用户状态 0:启用  1:停用
				task.setStatus(0);
				taskTypeService.updateByPrimaryKey(task);
				//修改日志
		        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, task,CommonLogImpl.TASK);
				return new ResponseRestEntity<TaskType>(HttpRestStatus.OK);
			}
			
			// 用户停用
			@ApiOperation(value = "enable the specified TaskType", notes = "")
			@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
			public ResponseRestEntity<TaskType> disableSysUser(@PathVariable("id") String id) {

				TaskType task = taskTypeService.selectByPrimaryKey(id);
				if (task == null) {
					return new ResponseRestEntity<TaskType>(HttpRestStatus.NOT_FOUND);
				}
				//改变用户状态 0:启用  1:停用
				task.setStatus(1);
				taskTypeService.updateByPrimaryKey(task);
				//修改日志
		        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, task,CommonLogImpl.TASK);
				return new ResponseRestEntity<TaskType>(HttpRestStatus.OK);
			}
}