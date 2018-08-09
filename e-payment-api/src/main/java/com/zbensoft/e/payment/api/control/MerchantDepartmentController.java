package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.zbensoft.e.payment.api.service.api.MerchantDepartmentService;
import com.zbensoft.e.payment.db.domain.MerchantDepartment;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantDepartment")
@RestController
public class MerchantDepartmentController {
	@Autowired
	MerchantDepartmentService merchantDepartmentService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_SELLER_E_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query MerchantDepartment，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantDepartment>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MerchantDepartment merchantDepartment = new MerchantDepartment();
		merchantDepartment.setMerchantDepartmentId(id);
		merchantDepartment.setUserId(userId);
		merchantDepartment.setName(name);
		
		int count = merchantDepartmentService.count(merchantDepartment);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantDepartment>>(new ArrayList<MerchantDepartment>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantDepartment> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantDepartmentService.selectPage(merchantDepartment);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantDepartmentService.selectPage(merchantDepartment);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantDepartment>>(new ArrayList<MerchantDepartment>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantDepartment>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_SELLER_E_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query MerchantDepartment", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantDepartment> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantDepartment merchantDepartment = merchantDepartmentService.selectByPrimaryKey(id);
		if (merchantDepartment == null) {
			return new ResponseRestEntity<MerchantDepartment>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantDepartment>(merchantDepartment, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Add MerchantDepartment", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantDepartment(@Valid @RequestBody MerchantDepartment merchantDepartment,BindingResult result, UriComponentsBuilder ucBuilder) {
		merchantDepartment.setMerchantDepartmentId(IDGenerate.generateCommOne(IDGenerate.MERCHANT_DEPARTMENT));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantDepartmentService.insert(merchantDepartment);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantDepartment,CommonLogImpl.MERCHANT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantDepartment/{id}").buildAndExpand(merchantDepartment.getMerchantDepartmentId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Edit MerchantDepartment", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantDepartment> updateMerchantDepartment(@PathVariable("id") String id,@Valid @RequestBody MerchantDepartment merchantDepartment, BindingResult result) {

		MerchantDepartment currentMerchantDepartment = merchantDepartmentService.selectByPrimaryKey(id);

		if (currentMerchantDepartment == null) {
			return new ResponseRestEntity<MerchantDepartment>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentMerchantDepartment.setUserId(merchantDepartment.getUserId());
		

		currentMerchantDepartment.setName(merchantDepartment.getName());
		currentMerchantDepartment.setRemark(merchantDepartment.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantDepartment>(currentMerchantDepartment,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantDepartmentService.updateByPrimaryKey(currentMerchantDepartment);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantDepartment,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantDepartment>(currentMerchantDepartment, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Edit Part MerchantDepartment", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantDepartment> updateMerchantDepartmentSelective(@PathVariable("id") String id,
			@RequestBody MerchantDepartment merchantDepartment) {

		MerchantDepartment currentMerchantDepartment = merchantDepartmentService.selectByPrimaryKey(id);

		if (currentMerchantDepartment == null) {
			return new ResponseRestEntity<MerchantDepartment>(HttpRestStatus.NOT_FOUND);
		}
		merchantDepartment.setMerchantDepartmentId(id);
		merchantDepartmentService.updateByPrimaryKeySelective(merchantDepartment);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantDepartment,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantDepartment>(currentMerchantDepartment, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Delete MerchantDepartment", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantDepartment> deleteMerchantDepartment(@PathVariable("id") String id) {

		MerchantDepartment merchantDepartment = merchantDepartmentService.selectByPrimaryKey(id);
		if (merchantDepartment == null) {
			return new ResponseRestEntity<MerchantDepartment>(HttpRestStatus.NOT_FOUND);
		}

		merchantDepartmentService.deleteByPrimaryKey(id);
		//删除日志开始
		MerchantDepartment merchant = new MerchantDepartment();
		merchant.setMerchantDepartmentId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
		return new ResponseRestEntity<MerchantDepartment>(HttpRestStatus.NO_CONTENT);
	}

}