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

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.FraultConsumerService;
import com.zbensoft.e.payment.db.domain.FraultConsumer;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultConsumer")
@RestController
public class FraultConsumerController {

	@Autowired
	FraultConsumerService fraultConsumerService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@ApiOperation(value = "Query FraultConsumer,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultConsumer>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String consumerId,
			@RequestParam(required = false) Integer suspiciouType, @RequestParam(required = false) Integer sourceType,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String remark ,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultConsumer addr = new FraultConsumer();
		addr.setFraultConsumerId(id);
		addr.setConsumerId(consumerId);
		addr.setSuspiciouType(suspiciouType);
		addr.setStatus(status);
		addr.setSourceType(sourceType);;
		addr.setRemark(remark);

		int count = fraultConsumerService.count(addr);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultConsumer>>(new ArrayList<FraultConsumer>(), HttpRestStatus.NOT_FOUND);
		}

		List<FraultConsumer> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultConsumerService.selectPage(addr);
		} else {
			list = fraultConsumerService.selectPage(addr);
		}

		return new ResponseRestEntity<List<FraultConsumer>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query FraultConsumer", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultConsumer> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultConsumer addr = fraultConsumerService.selectByPrimaryKey(id);
		if (addr == null) {
			return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultConsumer>(addr, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultConsumer", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtask(@RequestBody FraultConsumer addr,BindingResult result,  UriComponentsBuilder ucBuilder) {
		addr.setFraultConsumerId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_CONSUMER));     
		addr.setCreateTime(PageHelperUtil.getCurrentDate());  
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
				fraultConsumerService.insert(addr);
				//新增日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, addr,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/FraultConsumer/{id}").buildAndExpand(addr.getFraultConsumerId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultConsumer", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultConsumer> updatetask(@PathVariable("id") String id, @RequestBody FraultConsumer addr) {

		FraultConsumer type = fraultConsumerService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setFraultConsumerId(addr.getFraultConsumerId());
		type.setConsumerId(addr.getConsumerId());
		type.setSuspiciouType(addr.getSuspiciouType());
		type.setSourceType(addr.getSourceType());
		type.setStatus(addr.getStatus());
		type.setRemark(addr.getRemark());
		
		fraultConsumerService.updateByPrimaryKey(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultConsumer>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultConsumer", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultConsumer> updatetaskSelective(@PathVariable("id") String id, @RequestBody FraultConsumer addr) {

		FraultConsumer type = fraultConsumerService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.NOT_FOUND);
		}
		fraultConsumerService.updateByPrimaryKeySelective(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultConsumer>(type, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultConsumer", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultConsumer> deletetask(@PathVariable("id") String id) {

		FraultConsumer task = fraultConsumerService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.NOT_FOUND);
		}

		fraultConsumerService.deleteByPrimaryKey(id);
		//删除日志开始
		FraultConsumer delBean = new FraultConsumer();
		delBean.setFraultConsumerId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.NO_CONTENT);
	}
	// 用户启用
		@ApiOperation(value = "enable the specified FraultConsumer", notes = "")
		@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<FraultConsumer> enableTask(@PathVariable("id") String id) {

			FraultConsumer task = fraultConsumerService.selectByPrimaryKey(id);
			if (task == null) {
				return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(0);
			fraultConsumerService.updateByPrimaryKey(task);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, task,CommonLogImpl.FRAULT_MANAGEMENT);
			return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.OK);
		}
		
		// 用户停用
		@ApiOperation(value = "enable the specified FraultConsumer", notes = "")
		@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<FraultConsumer> disableSysUser(@PathVariable("id") String id) {

			FraultConsumer task = fraultConsumerService.selectByPrimaryKey(id);
			if (task == null) {
				return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(1);
			fraultConsumerService.updateByPrimaryKey(task);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, task,CommonLogImpl.FRAULT_MANAGEMENT);
			return new ResponseRestEntity<FraultConsumer>(HttpRestStatus.OK);
		}
}
