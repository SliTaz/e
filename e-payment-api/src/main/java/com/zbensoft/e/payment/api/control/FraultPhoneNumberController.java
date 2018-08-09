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
import com.zbensoft.e.payment.api.service.api.FraultPhoneNumberService;
import com.zbensoft.e.payment.db.domain.FraultPhoneNumber;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultPhoneNumber")
@RestController
public class FraultPhoneNumberController {
	@Autowired
	FraultPhoneNumberService fraultPhoneNumberService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@ApiOperation(value = "Query FraultPhoneNumber,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultPhoneNumber>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String phoneNumber,
			@RequestParam(required = false) Integer suspiciouType, @RequestParam(required = false) Integer sourceType,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String remark ,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultPhoneNumber addr = new FraultPhoneNumber();
		addr.setFraultPhoneNumberId(id);
		addr.setPhoneNumber(phoneNumber);
		addr.setSuspiciouType(suspiciouType);
		addr.setStatus(status);
		addr.setSourceType(sourceType);;
		addr.setRemark(remark);

		int count = fraultPhoneNumberService.count(addr);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultPhoneNumber>>(new ArrayList<FraultPhoneNumber>(), HttpRestStatus.NOT_FOUND);
		}

		List<FraultPhoneNumber> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultPhoneNumberService.selectPage(addr);
		} else {
			list = fraultPhoneNumberService.selectPage(addr);
		}

		return new ResponseRestEntity<List<FraultPhoneNumber>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query FraultPhoneNumber", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultPhoneNumber> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultPhoneNumber addr = fraultPhoneNumberService.selectByPrimaryKey(id);
		if (addr == null) {
			return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultPhoneNumber>(addr, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultPhoneNumber", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtask(@RequestBody FraultPhoneNumber addr,BindingResult result,  UriComponentsBuilder ucBuilder) {
		addr.setFraultPhoneNumberId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_PHONE_NUMBER));  
		addr.setCreateTime(PageHelperUtil.getCurrentDate());
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
				fraultPhoneNumberService.insert(addr);
				//新增日志
		        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, addr,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/FraultPhoneNumber/{id}").buildAndExpand(addr.getFraultPhoneNumberId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultPhoneNumber", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultPhoneNumber> updatetask(@PathVariable("id") String id, @RequestBody FraultPhoneNumber addr) {

		FraultPhoneNumber type = fraultPhoneNumberService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setFraultPhoneNumberId(addr.getFraultPhoneNumberId());
		type.setPhoneNumber(addr.getPhoneNumber());
		type.setSuspiciouType(addr.getSuspiciouType());
		type.setSourceType(addr.getSourceType());
		type.setStatus(addr.getStatus());
		type.setRemark(addr.getRemark());
		
		fraultPhoneNumberService.updateByPrimaryKey(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultPhoneNumber>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultPhoneNumber", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultPhoneNumber> updatetaskSelective(@PathVariable("id") String id, @RequestBody FraultPhoneNumber addr) {

		FraultPhoneNumber type = fraultPhoneNumberService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.NOT_FOUND);
		}
		fraultPhoneNumberService.updateByPrimaryKeySelective(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultPhoneNumber>(type, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultPhoneNumber", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultPhoneNumber> deletetask(@PathVariable("id") String id) {

		FraultPhoneNumber task = fraultPhoneNumberService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.NOT_FOUND);
		}

		fraultPhoneNumberService.deleteByPrimaryKey(id);
		//删除日志开始
		FraultPhoneNumber delBean = new FraultPhoneNumber();
		delBean.setFraultPhoneNumberId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.NO_CONTENT);
	}
	// 用户启用
		@ApiOperation(value = "enable the specified FraultPhoneNumber", notes = "")
		@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<FraultPhoneNumber> enableTask(@PathVariable("id") String id) {

			FraultPhoneNumber task = fraultPhoneNumberService.selectByPrimaryKey(id);
			if (task == null) {
				return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(0);
			fraultPhoneNumberService.updateByPrimaryKey(task);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, task,CommonLogImpl.FRAULT_MANAGEMENT);
			return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.OK);
		}
		
		// 用户停用
		@ApiOperation(value = "enable the specified FraultPhoneNumber", notes = "")
		@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<FraultPhoneNumber> disableSysUser(@PathVariable("id") String id) {

			FraultPhoneNumber task = fraultPhoneNumberService.selectByPrimaryKey(id);
			if (task == null) {
				return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(1);
			fraultPhoneNumberService.updateByPrimaryKey(task);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, task,CommonLogImpl.FRAULT_MANAGEMENT);
			return new ResponseRestEntity<FraultPhoneNumber>(HttpRestStatus.OK);
		}
}
