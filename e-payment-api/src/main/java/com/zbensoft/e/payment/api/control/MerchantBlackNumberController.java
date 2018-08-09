package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantBlackNumberHisService;
import com.zbensoft.e.payment.api.service.api.MerchantBlackNumberService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.MerchantBlackNumber;
import com.zbensoft.e.payment.db.domain.MerchantBlackNumberHis;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantBlackNumber")
@RestController
public class MerchantBlackNumberController {
	@Autowired
	MerchantBlackNumberService merchantBlackNumberService;
	@Autowired
	MerchantBlackNumberHisService merchantBlackNumberHisService;
	@Autowired
	MerchantUserService merchantUserService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_SELLER_B_N_Q')")
	@ApiOperation(value = "Query MerchantBlackNumber，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantBlackNumber>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String createReason,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MerchantBlackNumber merchantBlackNumber = new MerchantBlackNumber();
		
		
		id = CommonFun.getRelVid(id);
		merchantBlackNumber.setUserId(id);
		merchantBlackNumber.setCreateReason(createReason);
		
		int count = merchantBlackNumberService.count(merchantBlackNumber);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantBlackNumber>>(new ArrayList<MerchantBlackNumber>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantBlackNumber> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantBlackNumberService.selectPage(merchantBlackNumber);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantBlackNumberService.selectPage(merchantBlackNumber);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantBlackNumber>>(new ArrayList<MerchantBlackNumber>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantBlackNumber>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_Q')")
	@ApiOperation(value = "Query MerchantBlackNumber", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantBlackNumber> selectByPrimaryKey(@PathVariable("id") String id) {
		id = CommonFun.getRelVid(id);
		MerchantBlackNumber merchantBlackNumber = merchantBlackNumberService.selectByPrimaryKey(id);
		if (merchantBlackNumber == null) {
			return new ResponseRestEntity<MerchantBlackNumber>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantBlackNumber>(merchantBlackNumber, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_E')")
	@ApiOperation(value = "Add MerchantBlackNumber", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantBlackNumber(@Valid @RequestBody MerchantBlackNumber merchantBlackNumber,BindingResult result, UriComponentsBuilder ucBuilder) {
		MerchantUser merchantUser = merchantUserService.selectByIdNumber(CommonFun.getRelVid(merchantBlackNumber.getUserId()));//VID
		if(merchantUser==null){
			return new ResponseRestEntity<Void>(HttpRestStatus.MERCHANT_NOT_FOUND,
					localeMessageSourceService.getMessage("common.select.merchant.message"));
		}
/*		merchantBlackNumber.setUserId(merchantUser.getClapStoreNo());*/
		merchantBlackNumber.setCreateTime(PageHelperUtil.getCurrentDate());
		merchantBlackNumber.setUserId(CommonFun.getRelVid(merchantBlackNumber.getUserId()));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		if (merchantBlackNumberService.isExist(merchantBlackNumber)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,
					localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		merchantBlackNumberService.insert(merchantBlackNumber);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantBlackNumber,CommonLogImpl.MERCHANT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantBlackNumber/{id}").buildAndExpand(merchantBlackNumber.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_E')")
	@ApiOperation(value = "Edit MerchantBlackNumber", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantBlackNumber> updateMerchantBlackNumber(@PathVariable("id") String id,@Valid @RequestBody MerchantBlackNumber merchantBlackNumber, BindingResult result) {
		id = CommonFun.getRelVid(id);
		MerchantBlackNumber currentMerchantBlackNumber = merchantBlackNumberService.selectByPrimaryKey(id);

		if (currentMerchantBlackNumber == null) {
			return new ResponseRestEntity<MerchantBlackNumber>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentMerchantBlackNumber.setCreateReason(merchantBlackNumber.getCreateReason());

		
		currentMerchantBlackNumber.setRemark(merchantBlackNumber.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantBlackNumber>(currentMerchantBlackNumber,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantBlackNumberService.updateByPrimaryKey(currentMerchantBlackNumber);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantBlackNumber,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantBlackNumber>(currentMerchantBlackNumber, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_E')")
	@ApiOperation(value = "Edit Part MerchantBlackNumber", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantBlackNumber> updateMerchantBlackNumberSelective(@PathVariable("id") String id,
			@RequestBody MerchantBlackNumber merchantBlackNumber) {
		id = CommonFun.getRelVid(id);
		MerchantBlackNumber currentMerchantBlackNumber = merchantBlackNumberService.selectByPrimaryKey(id);

		if (currentMerchantBlackNumber == null) {
			return new ResponseRestEntity<MerchantBlackNumber>(HttpRestStatus.NOT_FOUND);
		}
		merchantBlackNumber.setUserId(id);
		merchantBlackNumberService.updateByPrimaryKeySelective(merchantBlackNumber);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantBlackNumber,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantBlackNumber>(currentMerchantBlackNumber, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_E')")
	@ApiOperation(value = "Delete MerchantBlackNumber", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantBlackNumber> deleteMerchantBlackNumber(@PathVariable("id") String id) {
		id = CommonFun.getRelVid(id);
		MerchantBlackNumber merchantBlackNumber = merchantBlackNumberService.selectByPrimaryKey(id);
		if (merchantBlackNumber == null) {
			return new ResponseRestEntity<MerchantBlackNumber>(HttpRestStatus.NOT_FOUND);
		}

		merchantBlackNumberService.deleteByPrimaryKey(id);
		//删除日志开始
		MerchantBlackNumber merchant = new MerchantBlackNumber();
		merchant.setUserId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
    	//删除在MerchantBlackNumberHis添加记录
    	MerchantBlackNumberHis merchantBlackNumberHis = new MerchantBlackNumberHis();
    	merchantBlackNumberHis.setMerchantBlackNumberHisId(IDGenerate.generateCommTwo(IDGenerate.MERCHANT_BLACK_NUMBER_HIS));
    	merchantBlackNumberHis.setUserId(merchantBlackNumber.getUserId());
    	merchantBlackNumberHis.setCreateReason(merchantBlackNumber.getCreateReason());
    	merchantBlackNumberHis.setCreateTime(merchantBlackNumber.getCreateTime());
    	merchantBlackNumberHis.setDeleteTime(PageHelperUtil.getCurrentDate());
    	merchantBlackNumberHisService.insert(merchantBlackNumberHis);
		return new ResponseRestEntity<MerchantBlackNumber>(HttpRestStatus.NO_CONTENT);
	}

}