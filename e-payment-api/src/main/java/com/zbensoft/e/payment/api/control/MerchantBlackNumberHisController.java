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
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantBlackNumberHisService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.ConsumerBlackNumberHis;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MerchantBlackNumberHis;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantBlackNumberHis")
@RestController
public class MerchantBlackNumberHisController {
	@Autowired
	MerchantBlackNumberHisService merchantBlackNumberHisService;
	@Autowired
	MerchantUserService merchantUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_SELLER_B_N_H_Q')")
	@ApiOperation(value = "Query MerchantBlackNumberHis，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantBlackNumberHis>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MerchantBlackNumberHis merchantBlackNumberHis = new MerchantBlackNumberHis();
		// 输入idNumber查询
		userId = CommonFun.getRelVid(userId);
				if ((userId == null || "".equals(userId)) && (id == null || "".equals(id))) {
					return new ResponseRestEntity<List<MerchantBlackNumberHis>>(new ArrayList<MerchantBlackNumberHis>(),
							HttpRestStatus.NOT_FOUND);
				}
		merchantBlackNumberHis.setMerchantBlackNumberHisId(id);
		merchantBlackNumberHis.setUserId(userId);
	
		
		int count = merchantBlackNumberHisService.count(merchantBlackNumberHis);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantBlackNumberHis>>(new ArrayList<MerchantBlackNumberHis>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantBlackNumberHis> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantBlackNumberHisService.selectPage(merchantBlackNumberHis);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantBlackNumberHisService.selectPage(merchantBlackNumberHis);
		}
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantBlackNumberHis>>(new ArrayList<MerchantBlackNumberHis>(),HttpRestStatus.NOT_FOUND);
		}
	
		return new ResponseRestEntity<List<MerchantBlackNumberHis>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_H_Q')")
	@ApiOperation(value = "Query MerchantBlackNumberHis", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantBlackNumberHis> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantBlackNumberHis merchantBlackNumberHis = merchantBlackNumberHisService.selectByPrimaryKey(id);
		if (merchantBlackNumberHis == null) {
			return new ResponseRestEntity<MerchantBlackNumberHis>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantBlackNumberHis>(merchantBlackNumberHis, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_H_E')")
	@ApiOperation(value = "Add MerchantBlackNumberHis", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createMerchantBlackNumberHis(@Valid @RequestBody MerchantBlackNumberHis merchantBlackNumberHis,BindingResult result, UriComponentsBuilder ucBuilder) {
		
		MerchantUser merchantUser = merchantUserService.selectByIdNumber(CommonFun.getRelVid(merchantBlackNumberHis.getUserId()));
		if(merchantUser==null){
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,
					localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		merchantBlackNumberHis.setMerchantBlackNumberHisId(IDGenerate.generateCommTwo(IDGenerate.MERCHANT_BLACK_NUMBER_HIS));
		merchantBlackNumberHis.setCreateTime(PageHelperUtil.getCurrentDate());
		merchantBlackNumberHis.setDeleteTime(PageHelperUtil.getCurrentDate());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantBlackNumberHisService.insert(merchantBlackNumberHis);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantBlackNumberHis,CommonLogImpl.MERCHANT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantBlackNumberHis/{id}").buildAndExpand(merchantBlackNumberHis.getMerchantBlackNumberHisId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit MerchantBlackNumberHis", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantBlackNumberHis> updateMerchantBlackNumberHis(@PathVariable("id") String id,@Valid @RequestBody MerchantBlackNumberHis merchantBlackNumberHis, BindingResult result) {

		MerchantBlackNumberHis currentMerchantBlackNumberHis = merchantBlackNumberHisService.selectByPrimaryKey(id);

		if (currentMerchantBlackNumberHis == null) {
			return new ResponseRestEntity<MerchantBlackNumberHis>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentMerchantBlackNumberHis.setUserId(merchantBlackNumberHis.getUserId());
		currentMerchantBlackNumberHis.setCreateReason(merchantBlackNumberHis.getCreateReason());

		currentMerchantBlackNumberHis.setDeleteReason(merchantBlackNumberHis.getDeleteReason());
		currentMerchantBlackNumberHis.setRemark(merchantBlackNumberHis.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantBlackNumberHis>(currentMerchantBlackNumberHis,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantBlackNumberHisService.updateByPrimaryKey(currentMerchantBlackNumberHis);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantBlackNumberHis,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantBlackNumberHis>(currentMerchantBlackNumberHis, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_H_E')")
	@ApiOperation(value = "Edit Part MerchantBlackNumberHis", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantBlackNumberHis> updateMerchantBlackNumberHisSelective(@PathVariable("id") String id,
			@RequestBody MerchantBlackNumberHis merchantBlackNumberHis) {

		MerchantBlackNumberHis currentMerchantBlackNumberHis = merchantBlackNumberHisService.selectByPrimaryKey(id);

		if (currentMerchantBlackNumberHis == null) {
			return new ResponseRestEntity<MerchantBlackNumberHis>(HttpRestStatus.NOT_FOUND);
		}
		merchantBlackNumberHis.setMerchantBlackNumberHisId(id);
		merchantBlackNumberHisService.updateByPrimaryKeySelective(merchantBlackNumberHis);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantBlackNumberHis,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantBlackNumberHis>(currentMerchantBlackNumberHis, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_SELLER_B_N_H_E')")
	@ApiOperation(value = "Delete MerchantBlackNumberHis", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantBlackNumberHis> deleteMerchantBlackNumberHis(@PathVariable("id") String id) {

		MerchantBlackNumberHis merchantBlackNumberHis = merchantBlackNumberHisService.selectByPrimaryKey(id);
		if (merchantBlackNumberHis == null) {
			return new ResponseRestEntity<MerchantBlackNumberHis>(HttpRestStatus.NOT_FOUND);
		}

		merchantBlackNumberHisService.deleteByPrimaryKey(id);
		//删除日志开始
		MerchantBlackNumberHis merchant = new MerchantBlackNumberHis();
		merchant.setMerchantBlackNumberHisId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
		return new ResponseRestEntity<MerchantBlackNumberHis>(HttpRestStatus.NO_CONTENT);
	}
	//批量
	@PreAuthorize("hasRole('R_SELLER_B_N_H_E')")
	@ApiOperation(value = "Delete Many MerchantBlackNumberHiss", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantBlackNumberHis> deleteMerchantBlackNumberHisMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        	  merchantBlackNumberHisService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<MerchantBlackNumberHis>(HttpRestStatus.NO_CONTENT);
	}

}