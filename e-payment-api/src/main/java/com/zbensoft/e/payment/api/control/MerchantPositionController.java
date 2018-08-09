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
import com.zbensoft.e.payment.api.service.api.MerchantPositionService;
import com.zbensoft.e.payment.db.domain.MerchantPosition;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantPosition")
@RestController
public class MerchantPositionController {
	@Autowired
	MerchantPositionService merchantPositionService;
	
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_SELLER_E_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query MerchantPosition，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantPosition>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MerchantPosition merchantPosition = new MerchantPosition();
		merchantPosition.setMerchantPositionId(id);
		merchantPosition.setUserId(userId);
		merchantPosition.setName(name);
		
		int count = merchantPositionService.count(merchantPosition);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantPosition>>(new ArrayList<MerchantPosition>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantPosition> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantPositionService.selectPage(merchantPosition);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantPositionService.selectPage(merchantPosition);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantPosition>>(new ArrayList<MerchantPosition>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<MerchantPosition>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_SELLER_E_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query MerchantPosition", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantPosition> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantPosition merchantPosition = merchantPositionService.selectByPrimaryKey(id);
		if (merchantPosition == null) {
			return new ResponseRestEntity<MerchantPosition>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantPosition>(merchantPosition, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Add MerchantPosition", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantPosition(@Valid @RequestBody MerchantPosition merchantPosition,BindingResult result, UriComponentsBuilder ucBuilder) {
		merchantPosition.setMerchantPositionId(IDGenerate.generateCommOne(IDGenerate.MERCHANT_POSITION));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantPositionService.insert(merchantPosition);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantPosition,CommonLogImpl.MERCHANT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantPosition/{id}").buildAndExpand(merchantPosition.getMerchantPositionId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Edit MerchantPosition", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantPosition> updateMerchantPosition(@PathVariable("id") String id,@Valid @RequestBody MerchantPosition merchantPosition, BindingResult result) {

		MerchantPosition currentMerchantPosition = merchantPositionService.selectByPrimaryKey(id);

		if (currentMerchantPosition == null) {
			return new ResponseRestEntity<MerchantPosition>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentMerchantPosition.setUserId(merchantPosition.getUserId());
		

		currentMerchantPosition.setName(merchantPosition.getName());
		currentMerchantPosition.setRemark(merchantPosition.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantPosition>(currentMerchantPosition,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantPositionService.updateByPrimaryKey(currentMerchantPosition);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantPosition,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantPosition>(currentMerchantPosition, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_E_E')")
	@ApiOperation(value = "Edit Part MerchantPosition", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantPosition> updateMerchantPositionSelective(@PathVariable("id") String id,
			@RequestBody MerchantPosition merchantPosition) {

		MerchantPosition currentMerchantPosition = merchantPositionService.selectByPrimaryKey(id);

		if (currentMerchantPosition == null) {
			return new ResponseRestEntity<MerchantPosition>(HttpRestStatus.NOT_FOUND);
		}
		merchantPosition.setMerchantPositionId(id);
		merchantPositionService.updateByPrimaryKeySelective(merchantPosition);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantPosition,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantPosition>(currentMerchantPosition, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_E_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Delete MerchantPosition", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantPosition> deleteMerchantPosition(@PathVariable("id") String id) {

		MerchantPosition merchantPosition = merchantPositionService.selectByPrimaryKey(id);
		if (merchantPosition == null) {
			return new ResponseRestEntity<MerchantPosition>(HttpRestStatus.NOT_FOUND);
		}

		merchantPositionService.deleteByPrimaryKey(id);
		//删除日志开始
		MerchantPosition merchant = new MerchantPosition();
		merchant.setMerchantPositionId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
		return new ResponseRestEntity<MerchantPosition>(HttpRestStatus.NO_CONTENT);
	}

}