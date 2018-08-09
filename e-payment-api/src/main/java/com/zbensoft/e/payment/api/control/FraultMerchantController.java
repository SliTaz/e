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
import com.zbensoft.e.payment.api.service.api.FraultMerchantService;
import com.zbensoft.e.payment.db.domain.FraultMerchant;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/fraultMerchant")
@RestController
public class FraultMerchantController {

	@Autowired
	FraultMerchantService fraultMerchantService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	
	@ApiOperation(value = "Query FraultMerchant,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<FraultMerchant>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String merchantId,
			@RequestParam(required = false) Integer suspiciouType, @RequestParam(required = false) Integer sourceType,
			@RequestParam(required = false) Integer status, @RequestParam(required = false) String remark ,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		FraultMerchant addr = new FraultMerchant();
		addr.setFraultMerchantId(id);
		addr.setMerchantId(merchantId);
		addr.setSuspiciouType(suspiciouType);
		addr.setStatus(status);
		addr.setSourceType(sourceType);;
		addr.setRemark(remark);

		int count = fraultMerchantService.count(addr);
		if (count == 0) {
			return new ResponseRestEntity<List<FraultMerchant>>(new ArrayList<FraultMerchant>(), HttpRestStatus.NOT_FOUND);
		}

		List<FraultMerchant> list = null;

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = fraultMerchantService.selectPage(addr);
		} else {
			list = fraultMerchantService.selectPage(addr);
		}

		return new ResponseRestEntity<List<FraultMerchant>>(list, HttpRestStatus.OK, count, count);
	}

	@ApiOperation(value = "Query FraultMerchant", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<FraultMerchant> selectByPrimaryKey(@PathVariable("id") String id) {
		FraultMerchant addr = fraultMerchantService.selectByPrimaryKey(id);
		if (addr == null) {
			return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<FraultMerchant>(addr, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add FraultMerchant", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createtask(@RequestBody FraultMerchant addr,BindingResult result,  UriComponentsBuilder ucBuilder) {
		addr.setFraultMerchantId(IDGenerate.generateCommTwo(IDGenerate.FRAULT_MERCHANT));         	
		addr.setCreateTime(PageHelperUtil.getCurrentDate());
		// 校验
				if (result.hasErrors()) {
					List<ObjectError> list = result.getAllErrors();
					return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
							HttpRestStatusFactory.createStatusMessage(list));
				}
				fraultMerchantService.insert(addr);
				//新增日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, addr,CommonLogImpl.FRAULT_MANAGEMENT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/FraultMerchant/{id}").buildAndExpand(addr.getFraultMerchantId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit FraultMerchant", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<FraultMerchant> updatetask(@PathVariable("id") String id, @RequestBody FraultMerchant addr) {

		FraultMerchant type = fraultMerchantService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		type.setFraultMerchantId(addr.getFraultMerchantId());
		type.setMerchantId(addr.getMerchantId());
		type.setSuspiciouType(addr.getSuspiciouType());
		type.setSourceType(addr.getSourceType());
		type.setStatus(addr.getStatus());
		type.setRemark(addr.getRemark());
		
		fraultMerchantService.updateByPrimaryKey(type);
		//修改日志
         CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultMerchant>(type, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part FraultMerchant", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<FraultMerchant> updatetaskSelective(@PathVariable("id") String id, @RequestBody FraultMerchant addr) {

		FraultMerchant type = fraultMerchantService.selectByPrimaryKey(id);

		if (type == null) {
			return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.NOT_FOUND);
		}
		fraultMerchantService.updateByPrimaryKeySelective(type);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, type,CommonLogImpl.FRAULT_MANAGEMENT);
		return new ResponseRestEntity<FraultMerchant>(type, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete FraultMerchant", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<FraultMerchant> deletetask(@PathVariable("id") String id) {

		FraultMerchant task = fraultMerchantService.selectByPrimaryKey(id);
		if (task == null) {
			return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.NOT_FOUND);
		}

		fraultMerchantService.deleteByPrimaryKey(id);
		//删除日志开始
		FraultMerchant delBean = new FraultMerchant();
		delBean.setFraultMerchantId(id);              
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.FRAULT_MANAGEMENT);
		//删除日志结束
		return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.NO_CONTENT);
	}

	// 用户启用
		@ApiOperation(value = "enable the specified FraultMerchant", notes = "")
		@RequestMapping(value = "/enable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<FraultMerchant> enableTask(@PathVariable("id") String id) {

			FraultMerchant task = fraultMerchantService.selectByPrimaryKey(id);
			if (task == null) {
				return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(0);
			fraultMerchantService.updateByPrimaryKey(task);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_ACTIVATE, task,CommonLogImpl.FRAULT_MANAGEMENT);
			return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.OK);
		}
		
		// 用户停用
		@ApiOperation(value = "enable the specified FraultMerchant", notes = "")
		@RequestMapping(value = "/disable/{id}", method = RequestMethod.PUT)
		public ResponseRestEntity<FraultMerchant> disableSysUser(@PathVariable("id") String id) {

			FraultMerchant task = fraultMerchantService.selectByPrimaryKey(id);
			if (task == null) {
				return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.NOT_FOUND);
			}
			//改变用户状态 0:启用  1:停用
			task.setStatus(1);
			fraultMerchantService.updateByPrimaryKey(task);
			//修改日志
	        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INACTIVATE, task,CommonLogImpl.FRAULT_MANAGEMENT);
			return new ResponseRestEntity<FraultMerchant>(HttpRestStatus.OK);
		}
}
