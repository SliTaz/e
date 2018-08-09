package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.RabbitmqDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankService;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankTradeInfoService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.api.vo.errorHandling.ErrorHandlingVo;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.BankTradeInfo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBank;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankTradeInfo;
import com.zbensoft.e.payment.db.domain.TradeInfo;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/errorHandlingBank")
@RestController
public class ErrorHandlingBankController {
	@Autowired
	ErrorHandlingBankService errorHandlingBankService;
	@Autowired
	BankInfoService bankInfoService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@Autowired
	ErrorHandlingBankTradeInfoService errorHandlingBankTradeInfoService;

	@Autowired
	TradeInfoService tradeInfoService;
	
	AmqpTemplate rabbitTemplate = SpringBeanUtil.getBean(AmqpTemplate.class);
	
	@PreAuthorize("hasRole('R_REC_R_Q')")
	@ApiOperation(value = "Query ErrorHandlingBank，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ErrorHandlingBank>> selectPage(@RequestParam(required = false) String id, @RequestParam(required = false) String reconciliationTime, @RequestParam(required = false) String refNo,
			@RequestParam(required = false) String tradeSeq, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
		errorHandlingBank.setBankId(id);
		errorHandlingBank.setReconciliationTime(reconciliationTime);
		errorHandlingBank.setRefNo(refNo);
		errorHandlingBank.setTradeSeq(tradeSeq);

		List<ErrorHandlingBank> list = new ArrayList<ErrorHandlingBank>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = errorHandlingBankService.selectPage(errorHandlingBank);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = errorHandlingBankService.selectPage(errorHandlingBank);
		}

		int count = errorHandlingBankService.count(errorHandlingBank);
		// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ErrorHandlingBank>>(new ArrayList<ErrorHandlingBank>(), HttpRestStatus.NOT_FOUND);
		}
		List<ErrorHandlingBank> listNew = new ArrayList<ErrorHandlingBank>();
		for (ErrorHandlingBank bean : list) {
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			if (bankInfo != null) {
				bean.setBankName(bankInfo.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ErrorHandlingBank>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_REC_R_Q')")
	@ApiOperation(value = "Query ErrorHandlingBank", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ErrorHandlingBank> selectByPrimaryKey(@PathVariable("bankId") String bankId, @PathVariable("reconciliationTime") String reconciliationTime, @PathVariable("refNo") String refNo) {
		ErrorHandlingBank bean = new ErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);

		ErrorHandlingBank errorHandlingBank = errorHandlingBankService.selectByPrimaryKey(bean);

		if (errorHandlingBank == null) {
			return new ResponseRestEntity<ErrorHandlingBank>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ErrorHandlingBank>(errorHandlingBank, HttpRestStatus.OK);
	}

	/*
	 * @ApiOperation(value = "Add ErrorHandlingBank", notes = "")
	 * 
	 * @RequestMapping(value = "", method = RequestMethod.POST) public ResponseRestEntity<Void> createBank(@RequestBody ErrorHandlingBank errorHandlingBank, UriComponentsBuilder
	 * ucBuilder) {
	 * 
	 * errorHandlingBank.setErrorHandlingBankId(IDGenerate.generateCommTwo(IDGenerate.RECONCILIATION_BATCH));
	 * 
	 * errorHandlingBankService.insert(errorHandlingBank);
	 * 
	 * HttpHeaders headers = new HttpHeaders(); headers.setLocation(ucBuilder.path("/errorHandlingBank/{id}") .buildAndExpand(errorHandlingBank.getErrorHandlingBankId()).toUri());
	 * return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message")); }
	 */

	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Edit ErrorHandlingBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PUT)
	public ResponseRestEntity<ErrorHandlingBank> updateBank(@PathVariable("bankId") String bankId, @PathVariable("reconciliationTime") String reconciliationTime, @PathVariable("refNo") String refNo,
			@RequestBody ErrorHandlingBank errorHandlingBank) {

		ErrorHandlingBank bean = new ErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);

		ErrorHandlingBank currentBank = errorHandlingBankService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ErrorHandlingBank>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentBank.setTradeSeq(errorHandlingBank.getTradeSeq());
		currentBank.setHanndlingResult(errorHandlingBank.getHanndlingResult());
		currentBank.setReason(errorHandlingBank.getReason());
		errorHandlingBankService.updateByPrimaryKey(currentBank);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentBank, CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ErrorHandlingBank>(currentBank, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Edit Part ErrorHandlingBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ErrorHandlingBank> updateBankSelective(@PathVariable("bankId") String bankId, @PathVariable("reconciliationTime") String reconciliationTime, @PathVariable("refNo") String refNo,
			@RequestBody ErrorHandlingBank errorHandlingBank) {

		ErrorHandlingBank bean = new ErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);

		ErrorHandlingBank currentBank = errorHandlingBankService.selectByPrimaryKey(bean);

		if (currentBank == null) {
			return new ResponseRestEntity<ErrorHandlingBank>(HttpRestStatus.NOT_FOUND);
		}

		errorHandlingBankService.updateByPrimaryKeySelective(errorHandlingBank);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, errorHandlingBank, CommonLogImpl.RECONCILIATION);
		return new ResponseRestEntity<ErrorHandlingBank>(currentBank, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "Delete ErrorHandlingBank", notes = "")
	@RequestMapping(value = "/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ErrorHandlingBank> deleteBank(@PathVariable("bankId") String bankId, @PathVariable("reconciliationTime") String reconciliationTime, @PathVariable("refNo") String refNo) {

		ErrorHandlingBank bean = new ErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);

		ErrorHandlingBank errorHandlingBank = errorHandlingBankService.selectByPrimaryKey(bean);
		if (errorHandlingBank == null) {
			return new ResponseRestEntity<ErrorHandlingBank>(HttpRestStatus.NOT_FOUND);
		}

		errorHandlingBankService.deleteByPrimaryKey(bean);

		return new ResponseRestEntity<ErrorHandlingBank>(HttpRestStatus.NO_CONTENT);
	}

	// 差错处理
	@PreAuthorize("hasRole('R_REC_R_E')")
	@ApiOperation(value = "error the specified govUser", notes = "")
	@RequestMapping(value = "/error/{bankId}/{reconciliationTime}/{refNo}", method = RequestMethod.PUT)
	public ResponseRestEntity<ErrorHandlingBank> error(@PathVariable("bankId") String bankId, @PathVariable("reconciliationTime") String reconciliationTime, @PathVariable("refNo") String refNo) {

		ErrorHandlingBank bean = new ErrorHandlingBank();
		bean.setBankId(bankId);
		bean.setReconciliationTime(reconciliationTime);
		bean.setRefNo(refNo);

		ErrorHandlingBank errorHandlingBank = errorHandlingBankService.selectByPrimaryKey(bean);
		if (errorHandlingBank == null) {
			return new ResponseRestEntity<ErrorHandlingBank>(HttpRestStatus.NOT_FOUND);
		}
		
		if (errorHandlingBank.getHanndlingResult() == MessageDef.ERROR_HANDLING_RESULT.UNHANDLING) {
			if (errorHandlingBank.getReason() == MessageDef.ERROR_HANDLING_REASON.BANK_NO_INFO) {
				if (errorHandlingBank.getTradeSeq() != null && errorHandlingBank.getTradeSeq().length() > 0) {
					TradeInfo tradeInfo = tradeInfoService.selectByPrimaryKey(errorHandlingBank.getTradeSeq());
					if (tradeInfo != null) {
						ErrorHandlingVo errorHandlingVo = new ErrorHandlingVo();
						errorHandlingVo.setTradeInfo(tradeInfo);
						errorHandlingVo.setBankId(bankId);
						errorHandlingVo.setReconciliationTime(reconciliationTime);

						rabbitTemplate.convertAndSend(RabbitmqDef.ERROR_HANDLING.EXCHANGE, null, errorHandlingVo);
					}
				}
			}
			if (errorHandlingBank.getReason() == MessageDef.ERROR_HANDLING_REASON.EPAY_NO_INFO) {
				ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo = new ErrorHandlingBankTradeInfo();
				errorHandlingBankTradeInfo.setBankId(bankId);
				errorHandlingBankTradeInfo.setReconciliationTime(reconciliationTime);
				errorHandlingBankTradeInfo.setRefNo(refNo);
				ErrorHandlingBankTradeInfo errorHandlingBankTradeInfoTmp = errorHandlingBankTradeInfoService.selectByPrimaryKey(errorHandlingBankTradeInfo);

				if (errorHandlingBankTradeInfoTmp != null) {
					BankTradeInfo bankTradeInfo = new BankTradeInfo();
					bankTradeInfo.setBankId(errorHandlingBankTradeInfoTmp.getBankId());
					bankTradeInfo.setChannelType(errorHandlingBankTradeInfoTmp.getChannelType());
					bankTradeInfo.setCurrencyType(errorHandlingBankTradeInfoTmp.getCurrencyType());
					bankTradeInfo.setRefNo(errorHandlingBankTradeInfoTmp.getRefNo());
					bankTradeInfo.setTransactionAmount(errorHandlingBankTradeInfoTmp.getTransactionAmount());
					bankTradeInfo.setTransactionDate(errorHandlingBankTradeInfoTmp.getTransactionDate());
					bankTradeInfo.setTransactionType(errorHandlingBankTradeInfoTmp.getTransactionType());
					bankTradeInfo.setVid(errorHandlingBankTradeInfoTmp.getVid());
					ErrorHandlingVo errorHandlingVo = new ErrorHandlingVo();
					errorHandlingVo.setBankTradeInfo(bankTradeInfo);
					errorHandlingVo.setBankId(bankId);
					errorHandlingVo.setReconciliationTime(reconciliationTime);

					rabbitTemplate.convertAndSend(RabbitmqDef.ERROR_HANDLING.EXCHANGE, null, errorHandlingVo);
				}
			}
		}

		errorHandlingBankService.updateByPrimaryKey(errorHandlingBank);
		// 修改日志

		return new ResponseRestEntity<ErrorHandlingBank>(HttpRestStatus.OK);
	}
}