package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingService;
import com.zbensoft.e.payment.db.domain.ErrorHandling;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/errorHandling")
@RestController
public class ErrorHandlingController {
	@Autowired
	ErrorHandlingService errorHandlingService;

	@ApiOperation(value = "Query ErrorHandling，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ErrorHandling>> selectPage(
			@RequestParam(required = false) String id,
			@RequestParam(required = false) String reconciliationBatchId,@RequestParam(required = false) String tradeSeq,
			@RequestParam(required = false) String type,@RequestParam(required = false) String status,
			@RequestParam(required = false) String consumptionName,@RequestParam(required = false) String merchantOrderNo,
			@RequestParam(required = false) String payGatewayId,@RequestParam(required = false) String payUserId,
			@RequestParam(required = false) String payUserName,@RequestParam(required = false) String recvUserId,
			@RequestParam(required = false) String recvUserName,@RequestParam(required = false) String createTimeStart,
			@RequestParam(required = false) String createTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		ErrorHandling errorHandling = new ErrorHandling();
		
		errorHandling.setErrorHandlingId(id);
		errorHandling.setReconciliationBatchId(reconciliationBatchId);
		errorHandling.setTradeSeq(tradeSeq);
		if(type!=null){
		errorHandling.setType(Integer.parseInt(type));
		}
		
		if(status!=null){
		errorHandling.setStatus(Integer.parseInt(status));
		}
		
		errorHandling.setConsumptionName(consumptionName);
		errorHandling.setMerchantOrderNo(merchantOrderNo);
		errorHandling.setPayGatewayId(payGatewayId);
		errorHandling.setPayUserId(payUserId);
		errorHandling.setPayUserName(payUserName);
		errorHandling.setRecvUserId(recvUserId);
		errorHandling.setRecvUserName(recvUserName);
		errorHandling.setCreateTimeStart(createTimeStart);
		errorHandling.setCreateTimeEnd(createTimeEnd);
		errorHandling.setDeleteFlag(0);
	
		List<ErrorHandling> list = new ArrayList<ErrorHandling>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = errorHandlingService.selectPage(errorHandling);

		} else {
			list = errorHandlingService.selectPage(errorHandling);
		}

		int count = errorHandlingService.count(errorHandling);
		// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ErrorHandling>>(new ArrayList<ErrorHandling>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<ErrorHandling>>(list, HttpRestStatus.OK,count,count);
	}

	@ApiOperation(value = "Query ErrorHandling", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ErrorHandling> selectByPrimaryKey(@PathVariable("id") String id) {
		ErrorHandling errorHandling = errorHandlingService.selectByPrimaryKey(id);
		if (errorHandling == null) {
			return new ResponseRestEntity<ErrorHandling>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ErrorHandling>(errorHandling, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Add ErrorHandling", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createHanding(@RequestBody ErrorHandling errorHandling, UriComponentsBuilder ucBuilder) {


		errorHandling.setErrorHandlingId(IDGenerate.generateCommTwo(IDGenerate.ERROR_HANDLING));
		errorHandling.setDeleteFlag(0);
		errorHandlingService.insert(errorHandling);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/errorHandling/{id}").buildAndExpand(errorHandling.getErrorHandlingId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED);
	}

	@ApiOperation(value = "Edit ErrorHandling", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ErrorHandling> updateHanding(@PathVariable("id") String id, @RequestBody ErrorHandling errorHandling) {

		ErrorHandling currentHanding = errorHandlingService.selectByPrimaryKey(id);

		if (currentHanding == null) {
			return new ResponseRestEntity<ErrorHandling>(HttpRestStatus.NOT_FOUND);
		}
        //修改
		currentHanding.setReconciliationBatchId(errorHandling.getReconciliationBatchId());
		currentHanding.setPayAppId(errorHandling.getPayAppId());
		currentHanding.setTradeSeq(errorHandling.getTradeSeq());
		currentHanding.setParentTradeSeq(errorHandling.getParentTradeSeq());
		currentHanding.setPayGatewayId(errorHandling.getPayGatewayId());
		currentHanding.setType(errorHandling.getType());
		currentHanding.setStatus(errorHandling.getStatus());
		currentHanding.setErrorCode(errorHandling.getErrorCode());
		currentHanding.setHaveRefund(errorHandling.getHaveRefund());
		currentHanding.setIsClose(errorHandling.getIsClose());	
		currentHanding.setConsumptionName(errorHandling.getConsumptionName());
		currentHanding.setMerchantOrderNo(errorHandling.getMerchantOrderNo());
		currentHanding.setPayUserId(errorHandling.getPayUserId());
		currentHanding.setPayUserName(errorHandling.getPayUserName());
		currentHanding.setPayBankId(errorHandling.getPayBankId());
		currentHanding.setPayBankName(errorHandling.getPayBankName());
		currentHanding.setPayBankCardNo(errorHandling.getPayBankOrderNo());
		currentHanding.setPayGetwayType(errorHandling.getPayGetwayType());
		currentHanding.setPayBankType(errorHandling.getPayBankType());
		currentHanding.setPayBankOrderNo(errorHandling.getPayBankOrderNo());	
		currentHanding.setPayAmount(errorHandling.getPayAmount());
		currentHanding.setPayFee(errorHandling.getPayFee());
		currentHanding.setPaySumAmount(errorHandling.getPaySumAmount());
		currentHanding.setPayStartMoney(errorHandling.getPayStartMoney());
		currentHanding.setPayEndMoney(errorHandling.getPayEndMoney());
		currentHanding.setPayBorrowLoanFlag(errorHandling.getPayBorrowLoanFlag());
		currentHanding.setRecvUserId(errorHandling.getRecvUserId());
		currentHanding.setRecvUserName(errorHandling.getRecvUserName());
		currentHanding.setRecvBankId(errorHandling.getRecvBankId());
		currentHanding.setRecvBankName(errorHandling.getRecvBankName());
		currentHanding.setRecvBankCardNo(errorHandling.getRecvBankCardNo());
		currentHanding.setRecvGetwayType(errorHandling.getRecvGetwayType());
		currentHanding.setRecvBankType(errorHandling.getRecvBankType());
		currentHanding.setRecvBankOrderNo(errorHandling.getRecvBankOrderNo());
		currentHanding.setRecvAmount(errorHandling.getRecvAmount());
		currentHanding.setRecvFee(errorHandling.getRecvFee());
		currentHanding.setRecvSumAmount(errorHandling.getRecvSumAmount());
		currentHanding.setRecvStartMoney(errorHandling.getRecvStartMoney());
		currentHanding.setRecvEndMoney(errorHandling.getRecvEndMoney());
		currentHanding.setPayBorrowLoanFlag(errorHandling.getPayBorrowLoanFlag());
		currentHanding.setCallbackUrl(errorHandling.getCallbackUrl());
		currentHanding.setCreateTime(errorHandling.getCreateTime());
		currentHanding.setPayTime(errorHandling.getPayTime());
		currentHanding.setEndTime(errorHandling.getEndTime());
		currentHanding.setReason(errorHandling.getReason());
		currentHanding.setHanndlingResult(errorHandling.getHanndlingResult());
		currentHanding.setHanndlingMsg(errorHandling.getHanndlingMsg());
		currentHanding.setRemark(errorHandling.getRemark());
		errorHandlingService.updateByPrimaryKey(currentHanding);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentHanding,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ErrorHandling>(currentHanding, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Edit Part ErrorHandling", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ErrorHandling> updateHandingSelective(@PathVariable("id") String id, @RequestBody ErrorHandling errorHandling) {

		ErrorHandling currentHanding = errorHandlingService.selectByPrimaryKey(id);

		if (currentHanding == null) {
			return new ResponseRestEntity<ErrorHandling>(HttpRestStatus.NOT_FOUND);
		}
		errorHandling.setErrorHandlingId(id);
        //修改
		currentHanding.setReconciliationBatchId(errorHandling.getReconciliationBatchId());
		currentHanding.setPayAppId(errorHandling.getPayAppId());
		currentHanding.setTradeSeq(errorHandling.getTradeSeq());
		currentHanding.setParentTradeSeq(errorHandling.getParentTradeSeq());
		currentHanding.setPayGatewayId(errorHandling.getPayGatewayId());
		currentHanding.setType(errorHandling.getType());
		currentHanding.setStatus(errorHandling.getStatus());
		currentHanding.setErrorCode(errorHandling.getErrorCode());
		currentHanding.setHaveRefund(errorHandling.getHaveRefund());
		currentHanding.setIsClose(errorHandling.getIsClose());	
		currentHanding.setConsumptionName(errorHandling.getConsumptionName());
		currentHanding.setMerchantOrderNo(errorHandling.getMerchantOrderNo());
		currentHanding.setPayUserId(errorHandling.getPayUserId());
		currentHanding.setPayUserName(errorHandling.getPayUserName());
		currentHanding.setPayBankId(errorHandling.getPayBankId());
		currentHanding.setPayBankName(errorHandling.getPayBankName());
		currentHanding.setPayBankCardNo(errorHandling.getPayBankOrderNo());
		currentHanding.setPayGetwayType(errorHandling.getPayGetwayType());
		currentHanding.setPayBankType(errorHandling.getPayBankType());
		currentHanding.setPayBankOrderNo(errorHandling.getPayBankOrderNo());	
		currentHanding.setPayAmount(errorHandling.getPayAmount());
		currentHanding.setPayFee(errorHandling.getPayFee());
		currentHanding.setPaySumAmount(errorHandling.getPaySumAmount());
		currentHanding.setPayStartMoney(errorHandling.getPayStartMoney());
		currentHanding.setPayEndMoney(errorHandling.getPayEndMoney());
		currentHanding.setPayBorrowLoanFlag(errorHandling.getPayBorrowLoanFlag());
		currentHanding.setRecvUserId(errorHandling.getRecvUserId());
		currentHanding.setRecvUserName(errorHandling.getRecvUserName());
		currentHanding.setRecvBankId(errorHandling.getRecvBankId());
		currentHanding.setRecvBankName(errorHandling.getRecvBankName());
		currentHanding.setRecvBankCardNo(errorHandling.getRecvBankCardNo());
		currentHanding.setRecvGetwayType(errorHandling.getRecvGetwayType());
		currentHanding.setRecvBankType(errorHandling.getRecvBankType());
		currentHanding.setRecvBankOrderNo(errorHandling.getRecvBankOrderNo());
		currentHanding.setRecvAmount(errorHandling.getRecvAmount());
		currentHanding.setRecvFee(errorHandling.getRecvFee());
		currentHanding.setRecvSumAmount(errorHandling.getRecvSumAmount());
		currentHanding.setRecvStartMoney(errorHandling.getRecvStartMoney());
		currentHanding.setRecvEndMoney(errorHandling.getRecvEndMoney());
		currentHanding.setPayBorrowLoanFlag(errorHandling.getPayBorrowLoanFlag());
		currentHanding.setCallbackUrl(errorHandling.getCallbackUrl());
		currentHanding.setCreateTime(errorHandling.getCreateTime());
		currentHanding.setPayTime(errorHandling.getPayTime());
		currentHanding.setEndTime(errorHandling.getEndTime());
		currentHanding.setReason(errorHandling.getReason());
		currentHanding.setHanndlingResult(errorHandling.getHanndlingResult());
		currentHanding.setHanndlingMsg(errorHandling.getHanndlingMsg());
		currentHanding.setRemark(errorHandling.getRemark());
		errorHandlingService.updateByPrimaryKeySelective(errorHandling);
		//修改日志
        CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, errorHandling,CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ErrorHandling>(currentHanding, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete ErrorHandling", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ErrorHandling> deleteHanding(@PathVariable("id") String id) {

		ErrorHandling errorHandling = errorHandlingService.selectByPrimaryKey(id);
		if (errorHandling == null) {
			return new ResponseRestEntity<ErrorHandling>(HttpRestStatus.NOT_FOUND);
		}
		errorHandling.setDeleteFlag(1);
		errorHandling.setDeleteTime(PageHelperUtil.getCurrentDate());// 删除时间
		errorHandling.setDeleteFlag(PageHelperUtil.DELETE_YES);// 改为已删除
		errorHandlingService.updateByPrimaryKeySelective(errorHandling);
		
		//删除日志开始
		ErrorHandling delBean = new ErrorHandling();
		delBean.setErrorHandlingId(id);              
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.RECONCILIATION);
		//删除日志结束
		return new ResponseRestEntity<ErrorHandling>(HttpRestStatus.NO_CONTENT);
	}

}