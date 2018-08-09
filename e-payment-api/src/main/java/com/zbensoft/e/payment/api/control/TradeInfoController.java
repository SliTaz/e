package com.zbensoft.e.payment.api.control;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.PayGatewayService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.DailyBill;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.PayGateway;
import com.zbensoft.e.payment.db.domain.TradeInfo;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/tradeInfo")
@RestController
public class TradeInfoController {

	private static final Logger log = LoggerFactory.getLogger(TradeInfoController.class);

	@Autowired
	TradeInfoService tradeInfoService;
	@Autowired
	MerchantUserService merchantUserService;
	@Autowired
	PayGatewayService payGatewayService;
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询交易信息，支持分页,管理用户使用
	@PreAuthorize("hasRole('R_TRADE_I_Q')")
	@ApiOperation(value = "Query TradeInfo，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<List<TradeInfo>> selectPage(@RequestParam(required = false) String trade_seq, @RequestParam(required = false) String payUserId, @RequestParam(required = false) String type,
			 @RequestParam(required = false) String bankId,@RequestParam(required = false) String status, @RequestParam(required = false) String merchantOrderNo, @RequestParam(required = false) String payAppId, @RequestParam(required = false) String recvUserId,
			@RequestParam(required = false) String payGetwayType, @RequestParam(required = false) String createTimeEnd, @RequestParam(required = false) String parentTradeSeq, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setTradeSeq(trade_seq);
		tradeInfo.setPayUserId(payUserId);

		// System.out.println("trade_seq=" +
		// trade_seq+"&consumption_name="+consumption_name+"&type="+type+"&status="+status+"&merchantOrderNo="+merchantOrderNo+"&payAppId="+payAppId+"&recvUserName="+recvUserName+"&payGetwayType="+payGetwayType);
		if ((trade_seq == null || "".equals(trade_seq)) && (merchantOrderNo == null || "".equals(merchantOrderNo)) && (type == null || "".equals(type)) && (status == null || "".equals(status)) && (payUserId == null || "".equals(payUserId))&& (recvUserId == null || "".equals(recvUserId))
				&& (createTimeEnd == null || "".equals(createTimeEnd)) && (parentTradeSeq == null || "".equals(parentTradeSeq))) {
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
		}
		try {
			if (type == null || "".equals(type)) {

			} else {
				tradeInfo.setType(Integer.valueOf(type));
			}

		} catch (Exception e) {
			log.error("", e);
		}

		try {
			if (status == null || "".equals(status)) {

			} else {
				tradeInfo.setStatus(Integer.valueOf(status));
			}
		} catch (Exception e) {
			log.error("", e);
		}
		tradeInfo.setMerchantOrderNo(merchantOrderNo);
		tradeInfo.setPayAppId(payAppId);
		tradeInfo.setRecvUserId(recvUserId);
		try {
			if (payGetwayType == null || "".equals(payGetwayType)) {

			} else {
				tradeInfo.setPayGetwayType(Integer.valueOf(payGetwayType));
			}
		} catch (Exception e) {
			log.error("", e);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// if (createTimeStart == null || "".equals(createTimeStart)) {
		// tradeInfo.setCreateTimeStartSer(sdf.format(new Date()));
		// } else {
		// tradeInfo.setCreateTimeStartSer(createTimeStart);
		// }

//		if (createTimeEnd == null || "".equals(createTimeEnd)) {
//			tradeInfo.setCreateTimeEndSer(sdf.format(new Date()));
//		} else {
//			tradeInfo.setCreateTimeEndSer(createTimeEnd);
//		}
		
		if(type.equals("11")){
			tradeInfo.setPayBankId(bankId);
		}
		if(type.equals("12")||type.equals("2")){
			tradeInfo.setRecvBankId(bankId);
		}
		
		tradeInfo.setCreateTimeEndSer(createTimeEnd);
		tradeInfo.setParentTradeSeq(parentTradeSeq);
		tradeInfo.setDeleteFlag(PageHelperUtil.DELETE_NO);

		List<TradeInfo> list = new ArrayList<TradeInfo>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = tradeInfoService.selectPage(tradeInfo);

		} else {
			list = tradeInfoService.selectPage(tradeInfo);
		}

		int count = tradeInfoService.count(tradeInfo);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
		} else {
			for (TradeInfo tradeInfo2 : list) {
				processAmout(tradeInfo2);
			}
		}
		List<TradeInfo> listNew = new ArrayList<TradeInfo>();
		for (TradeInfo bean : list) {
			PayGateway payGateway = payGatewayService.selectByPrimaryKey(bean.getRecvGatewayId());
			if (payGateway != null) {
				bean.setRecvGatewayName(payGateway.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<TradeInfo>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 交易信息详情
	@PreAuthorize("hasRole('R_TRADE_I_Q') or hasRole('R_REC_R_Q') or hasRole('R_REC_C_Q')")
	@ApiOperation(value = "Query TradeInfo，Support paging", notes = "")
	@RequestMapping(value = "/details", method = RequestMethod.POST)
	public ResponseRestEntity<List<TradeInfo>> selectPage(@RequestParam(required = false) String trade_seq, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setTradeSeq(trade_seq);

		tradeInfo.setDeleteFlag(PageHelperUtil.DELETE_NO);

		List<TradeInfo> list = new ArrayList<TradeInfo>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = tradeInfoService.selectPage(tradeInfo);

		} else {
			list = tradeInfoService.selectPage(tradeInfo);
		}

		int count = tradeInfoService.count(tradeInfo);
		// 分页 end
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
		}

		return new ResponseRestEntity<List<TradeInfo>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询交易信息
	@PreAuthorize("hasRole('R_TRADE_I_Q') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query TradeInfo", notes = "")
	@RequestMapping(value = "/{trade_seq}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<TradeInfo> selectByPrimaryKey(@PathVariable("trade_seq") String trade_seq) {
		TradeInfo tradeInfo = tradeInfoService.selectByPrimaryKey(trade_seq);
		if (tradeInfo == null) {
			return new ResponseRestEntity<TradeInfo>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<TradeInfo>(tradeInfo, HttpRestStatus.OK);
	}

	// 新增交易信息
	@PreAuthorize("hasRole('R_TRADE_I_E')")
	@ApiOperation(value = "Add TradeInfo", notes = "")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@RequestBody TradeInfo tradeInfo, UriComponentsBuilder ucBuilder) {

		tradeInfo.setTradeSeq(PageHelperUtil.getSeq());

		tradeInfo.setParentTradeSeq(PageHelperUtil.getSeq());
		tradeInfo.setCreateTime(PageHelperUtil.getCurrentDate());
		tradeInfo.setDeleteFlag(PageHelperUtil.DELETE_NO);

		// System.out.println(tradeInfo.getPayTime().toString());

		/*
		 * if (result.hasErrors()) { List<ObjectError> list = result.getAllErrors();
		 * 
		 * return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list)); }
		 */
		tradeInfoService.insert(tradeInfo);
		// 新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, tradeInfo, CommonLogImpl.FINANCE);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/tradeInfo/{trade_seq}").buildAndExpand(tradeInfo.getTradeSeq()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改交易信息
	@PreAuthorize("hasRole('R_TRADE_I_E') or hasRole('R_B_TRADE_I_E') or hasRole('R_S_TRADE_I_E') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Edit TradeInfo", notes = "")
	@RequestMapping(value = "{trade_seq}", method = RequestMethod.PUT)
	public ResponseRestEntity<TradeInfo> update(@PathVariable("trade_seq") String trade_seq, @RequestBody TradeInfo tradeInfo, @RequestParam(required = false) String userId) {

		TradeInfo currentTradeInfo = tradeInfoService.selectByPrimaryKey(trade_seq);

		if (currentTradeInfo == null) {
			return new ResponseRestEntity<TradeInfo>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentTradeInfo.setStatus(tradeInfo.getStatus());
		currentTradeInfo.setType(tradeInfo.getType());
		currentTradeInfo.setConsumptionName(tradeInfo.getConsumptionName());
		currentTradeInfo.setMerchantOrderNo(tradeInfo.getMerchantOrderNo());
		// currentTradeInfo.setBankOrderNo(tradeInfo.getBankOrderNo());
		currentTradeInfo.setPayUserId(tradeInfo.getPayUserId());
		currentTradeInfo.setPayUserName(tradeInfo.getPayUserName());
		currentTradeInfo.setPayEmployeeUserId(tradeInfo.getPayEmployeeUserId());
		currentTradeInfo.setPayEmployeeUserName(tradeInfo.getPayEmployeeUserName());
		currentTradeInfo.setPayAmount(tradeInfo.getPayAmount());
		currentTradeInfo.setPayFee(tradeInfo.getPayFee());
		currentTradeInfo.setPaySumAmount(tradeInfo.getPaySumAmount());
		currentTradeInfo.setPayStartMoney(tradeInfo.getPayStartMoney());
		currentTradeInfo.setPayEndMoney(tradeInfo.getPayEndMoney());
		currentTradeInfo.setPayBorrowLoanFlag(tradeInfo.getPayBorrowLoanFlag());
		currentTradeInfo.setRecvUserId(tradeInfo.getRecvUserId());
		currentTradeInfo.setRecvUserName(tradeInfo.getRecvUserName());
		currentTradeInfo.setRecvEmployeeUserId(tradeInfo.getRecvEmployeeUserId());
		currentTradeInfo.setRecvGatewayId(tradeInfo.getRecvGatewayId());
		currentTradeInfo.setRecvEmployeeUserName(tradeInfo.getRecvEmployeeUserName());
		currentTradeInfo.setRecvAmount(tradeInfo.getRecvAmount());
		currentTradeInfo.setRecvFee(tradeInfo.getRecvFee());
		currentTradeInfo.setRecvSumAmount(tradeInfo.getRecvSumAmount());
		currentTradeInfo.setRecvStartMoney(tradeInfo.getRecvStartMoney());
		currentTradeInfo.setRecvEndMoney(tradeInfo.getRecvEndMoney());
		currentTradeInfo.setRecvBorrowLoanFlag(tradeInfo.getRecvBorrowLoanFlag());
		currentTradeInfo.setPayBankId(tradeInfo.getPayBankId());
		currentTradeInfo.setRecvGatewayId(tradeInfo.getRecvGatewayId());
		currentTradeInfo.setPayGetwayType(tradeInfo.getPayGetwayType());
		currentTradeInfo.setPayBankType(tradeInfo.getPayBankType());
		currentTradeInfo.setPayBankCardNo(tradeInfo.getPayBankCardNo());
		currentTradeInfo.setCallbackUrl(tradeInfo.getCallbackUrl());
		currentTradeInfo.setPayTime(tradeInfo.getPayTime());
		currentTradeInfo.setEndTime(tradeInfo.getEndTime());
		currentTradeInfo.setRemark(tradeInfo.getRemark());
		currentTradeInfo.setCouponId(tradeInfo.getCouponId());
		currentTradeInfo.setPayAppId(tradeInfo.getPayAppId());
		currentTradeInfo.setPayGatewayId(tradeInfo.getPayGatewayId());
		currentTradeInfo.setHaveRefund(tradeInfo.getHaveRefund());
		currentTradeInfo.setIsClose(tradeInfo.getIsClose());
		currentTradeInfo.setPayBankName(tradeInfo.getPayBankName());
		currentTradeInfo.setPayBankOrderNo(tradeInfo.getPayBankOrderNo());
		currentTradeInfo.setRecvBankId(tradeInfo.getRecvBankId());
		currentTradeInfo.setRecvBankName(tradeInfo.getRecvBankName());
		currentTradeInfo.setRecvBankCardNo(tradeInfo.getRecvBankCardNo());
		currentTradeInfo.setRecvGetwayType(tradeInfo.getRecvGetwayType());
		currentTradeInfo.setRecvBankType(tradeInfo.getRecvBankType());
		currentTradeInfo.setRecvBankOrderNo(tradeInfo.getRecvBankOrderNo());
		currentTradeInfo.setPayBankCardHolerName(tradeInfo.getPayBankCardHolerName());
		currentTradeInfo.setRecvBankCardHolerName(tradeInfo.getRecvBankCardHolerName());

		currentTradeInfo.setErrorCode(tradeInfo.getErrorCode());

		/*
		 * if (result.hasErrors()) { List<ObjectError> list = result.getAllErrors(); return new
		 * ResponseRestEntity<TradeInfo>(currentTradeInfo,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list)); }
		 */

		tradeInfoService.remarkOrder(tradeInfo, userId);
		tradeInfoService.updateByPrimaryKey(currentTradeInfo);
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentTradeInfo, CommonLogImpl.FINANCE);
		return new ResponseRestEntity<TradeInfo>(currentTradeInfo, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分交易信息
	@PreAuthorize("hasRole('R_TRADE_I_E') or hasRole('R_B_TRADE_I_E') or hasRole('R_S_TRADE_I_E')")
	@ApiOperation(value = "Edit Part TradeInfo", notes = "")
	@RequestMapping(value = "{trade_seq}", method = RequestMethod.PATCH)
	public ResponseRestEntity<TradeInfo> updateSelective(@PathVariable("trade_seq") String trade_seq, @RequestBody TradeInfo tradeInfo) {

		TradeInfo currentTradeInfo = tradeInfoService.selectByPrimaryKey(trade_seq);

		if (currentTradeInfo == null) {
			return new ResponseRestEntity<TradeInfo>(HttpRestStatus.NOT_FOUND);
		}
		tradeInfo.setTradeSeq(trade_seq);
		tradeInfoService.updateByPrimaryKeySelective(tradeInfo);// ?
		// 修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, tradeInfo, CommonLogImpl.FINANCE);
		return new ResponseRestEntity<TradeInfo>(currentTradeInfo, HttpRestStatus.OK);
	}

	// 查询父账单
	@PreAuthorize("hasRole('R_TRADE_I_Q') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Edit Part TradeInfo", notes = "")
	@RequestMapping(value = "/selectTradeInfoByParentSeq", method = RequestMethod.GET)
	public ResponseRestEntity<List<TradeInfo>> selectTradeInfoByParentSeq(@RequestParam(required = false) String tradeSeq) {
		List<TradeInfo> tradeInfoList = tradeInfoService.getTradInfoByParentTradeSeq(tradeSeq);

		return new ResponseRestEntity<List<TradeInfo>>(tradeInfoList, HttpRestStatus.OK, "");

	}

	// 查询交易信息，支持分页
	@PreAuthorize("hasRole('R_B_TRADE_I_Q') or hasRole('R_S_TRADE_I_Q') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query TradeInfo，Support paging", notes = "")
	@RequestMapping(value = "/byUser", method = RequestMethod.GET)
	public ResponseRestEntity<List<TradeInfo>> selectPageByUser(@RequestParam(required = true) String userId, @RequestParam(required = false) String userName, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length, @RequestParam(required = false) String page, @RequestParam(required = false) String createTimeStart, @RequestParam(required = false) String createTimeEnd,
			@RequestParam(required = false) String type, @RequestParam(required = false) String status, @RequestParam(required = false) String deleteFlag, @RequestParam(required = false) String tradeSeq,
			@RequestParam(required = false) String employeeUserId) {
		try {

			TradeInfo tradeInfo = new TradeInfo();
			if (deleteFlag != null && MessageDef.DELETE_FLAG.DELETED_STRING.equals(deleteFlag)) {
				tradeInfo.setDeleteFlag(MessageDef.DELETE_FLAG.DELETED);
			} else {
				tradeInfo.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
			}
			if (userId != null && !userId.isEmpty()) {
				tradeInfo.setPayUserId(userId);
				tradeInfo.setRecvUserId(userId);
			} else {
				return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOTEMPTY, "用户名编号必须填写");
			}

			tradeInfo.setCreateTimeStartSer(createTimeStart);
			tradeInfo.setCreateTimeEndSer(createTimeEnd);
			if (type != null && !type.isEmpty()) {
				try {
					tradeInfo.setType(Integer.valueOf(type));
				} catch (Exception e) {
					return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.TYPE, "类型为数字");
				}
			}

			if (status != null && !status.isEmpty()) {
				try {
					tradeInfo.setStatus(Integer.valueOf(status));
				} catch (Exception e) {
					return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.TYPE, "状态为数字");
				}
			}

			if (tradeSeq != null && !tradeSeq.isEmpty()) {
				try {
					tradeInfo.setTradeSeq(tradeSeq);
				} catch (Exception e) {
					return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.TYPE, "序号为字符");
				}
			}
			tradeInfo.setRecvEmployeeUserId(employeeUserId);

			List<TradeInfo> list = new ArrayList<TradeInfo>();
			// 分页 start

			int pageNum = 1;
			int pageSize = 10;

			if (page != null && !page.isEmpty()) {
				pageNum = Integer.valueOf(page);
			} else {
				pageNum = PageHelperUtil.getPageNum(start, length);
				pageSize = PageHelperUtil.getPageSize(start, length);
			}
			PageHelper.startPage(pageNum, pageSize);
			list = tradeInfoService.selectPageByUser(tradeInfo);

			int count = tradeInfoService.countByUser(tradeInfo);
			// 分页 end

			if (list == null || list.isEmpty()) {
				return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
			}
			for (TradeInfo tradeInfo2 : list) {
				processAmout(tradeInfo2);
			}
			return new ResponseRestEntity<List<TradeInfo>>(list, HttpRestStatus.OK, count, count);
		} catch (Exception e) {
			log.error("selectPageByUser error", e);
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.UNKNOWN, "未知错误");
		}
	}

	private void processAmout(TradeInfo tradeInfo) {
		if (tradeInfo == null) {
			return;
		}
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
		if (tradeInfo.getType() == MessageDef.TRADE_TYPE.RECHARGE) {
			if (tradeInfo.getPayUserId().equals(tradeInfo.getRecvUserId())) {
				tradeInfo.setPayAmountView("+" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
				tradeInfo.setRecvAmountView("+" + formatAmount(decimalFormat.format(tradeInfo.getRecvAmount())));
			} else {
				if (tradeInfo.getPayBankCardNo() != null && !tradeInfo.getPayBankCardNo().isEmpty()) {
					tradeInfo.setPayAmountView(formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
				} else {
					tradeInfo.setPayAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
				}
				tradeInfo.setRecvAmountView("+" + formatAmount(decimalFormat.format(tradeInfo.getRecvAmount())));
			}
		}

		if (tradeInfo.getType() == MessageDef.TRADE_TYPE.CHARGE) {
			tradeInfo.setPayAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			tradeInfo.setRecvAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getRecvAmount())));
		}

		if (tradeInfo.getType() == MessageDef.TRADE_TYPE.TRANSFER) {
			tradeInfo.setPayAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			if (tradeInfo.getPayBankCardNo() != null && !tradeInfo.getPayBankCardNo().isEmpty()) {
				tradeInfo.setPayAmountView(formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			} else {
				tradeInfo.setPayAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			}
			tradeInfo.setRecvAmountView("+" + formatAmount(decimalFormat.format(tradeInfo.getRecvAmount())));
		}
		if (tradeInfo.getType() == MessageDef.TRADE_TYPE.TRANSFER_BANK) {
			tradeInfo.setPayAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			if (tradeInfo.getPayBankCardNo() != null && !tradeInfo.getPayBankCardNo().isEmpty()) {
				tradeInfo.setPayAmountView(formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			} else {
				tradeInfo.setPayAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			}
			tradeInfo.setRecvAmountView("" + formatAmount(decimalFormat.format(tradeInfo.getRecvAmount())));
		}
		if (tradeInfo.getType() == MessageDef.TRADE_TYPE.CONSUMPTION) {
			tradeInfo.setPayAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			tradeInfo.setRecvAmountView("+" + formatAmount(decimalFormat.format(tradeInfo.getRecvAmount())));
		}

		if (tradeInfo.getType() == MessageDef.TRADE_TYPE.BANK_RECHARGE) {
			tradeInfo.setPayAmountView("+" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			tradeInfo.setRecvAmountView("+" + formatAmount(decimalFormat.format(tradeInfo.getRecvAmount())));
		}

		if (tradeInfo.getType() == MessageDef.TRADE_TYPE.BANK_REVERSE) {
			tradeInfo.setPayAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getPayAmount())));
			tradeInfo.setRecvAmountView("-" + formatAmount(decimalFormat.format(tradeInfo.getRecvAmount())));
		}
	}

	private String formatAmount(String format) {
		if (format != null) {
			format = format.replaceAll("\\.", ":");
			format = format.replaceAll(",", ".");
			format = format.replaceAll(":", ",");
			return format;
		}
		return "";
	}

	// 查询交易信息
	@PreAuthorize("hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query TradeInfo", notes = "")
	@RequestMapping(value = "/getTradInfoDetail", method = RequestMethod.GET)
	public ResponseRestEntity<List<TradeInfo>> getTradInfoDetail(@RequestParam(required = false) String tradeSeq) {
		try {

			List<TradeInfo> list = new ArrayList<TradeInfo>();
			int count = 0;
			TradeInfo tradeInfo = tradeInfoService.getTradInfoByTradeSeq(tradeSeq);
			if (tradeInfo == null) {

			} else {
				list.add(tradeInfo);
				count = 1;
			}

			if (list == null || list.isEmpty()) {
				return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
			} else {
				for (TradeInfo tradeInfo2 : list) {
					processAmout(tradeInfo2);
				}
			}
			return new ResponseRestEntity<List<TradeInfo>>(list, HttpRestStatus.OK, count, count);
		} catch (Exception e) {
			log.error("selectPageByUser error", e);
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.UNKNOWN, "未知错误");
		}
	}

	// 删除指定交易信息
	@PreAuthorize("hasRole('R_TRADE_I_E') or hasRole('R_B_TRADE_I_E') or hasRole('R_S_TRADE_I_E') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "Delete TradeInfo", notes = "")
	@RequestMapping(value = "/{trade_seq}", method = RequestMethod.DELETE)
	public ResponseRestEntity<TradeInfo> delete(@PathVariable("trade_seq") String trade_seq, @RequestParam(required = false) String userId) {
		tradeInfoService.deleteByTradeInfo(trade_seq, userId);
		return new ResponseRestEntity<TradeInfo>(HttpRestStatus.NO_CONTENT);
	}

	// 还原
	@PreAuthorize("hasRole('R_TRADE_I_E') or hasRole('R_B_TRADE_I_E') or hasRole('R_S_TRADE_I_E') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "restoreTradeInfo", notes = "")
	@RequestMapping(value = "/restoreTradeInfo", method = RequestMethod.GET)
	public ResponseRestEntity<Void> restoreTradeInfo(@RequestParam(required = false) String trade_seq, @RequestParam(required = false) String userId) {
		tradeInfoService.restoreByPrimaryKey(trade_seq, userId);
		return new ResponseRestEntity<Void>(HttpRestStatus.OK, "成功");
	}

	// 永久删除
	@PreAuthorize("hasRole('R_TRADE_I_E') or hasRole('R_B_TRADE_I_E') or hasRole('R_S_TRADE_I_E') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@ApiOperation(value = "foreverDeleteTradeInfo", notes = "")
	@RequestMapping(value = "/foreverDeleteTradeInfo", method = RequestMethod.GET)
	public ResponseRestEntity<Void> foreverDeleteTradeInfo(@RequestParam(required = false) String trade_seq, @RequestParam(required = false) String userId) {
		tradeInfoService.foreverDeleteByPrimaryKey(trade_seq, userId);
		return new ResponseRestEntity<Void>(HttpRestStatus.OK, "成功");
	}

	// 查询日账单
	@PreAuthorize("hasRole('R_S_TRADE_I_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query DailyBill", notes = "")
	@RequestMapping(value = "/getDalyBill", method = RequestMethod.GET)
	public ResponseRestEntity<DailyBill> getDalyBill(@RequestParam(required = false) String user_id) {

		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
		if (user_id != null && !user_id.isEmpty()) {
			tradeInfo.setPayUserId(user_id);
			tradeInfo.setRecvUserId(user_id);
		} else {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOTEMPTY, "用户名编号必须填写");
		}

		tradeInfo.setCreateTimeStartSer(DateUtil.convertDateToString(new Date(System.currentTimeMillis()), DateUtil.DATE_FORMAT_ONE) + " 00:00:00");
		tradeInfo.setType(MessageDef.TRADE_TYPE.CONSUMPTION);
		List<TradeInfo> list = tradeInfoService.selectPageByUser(tradeInfo);

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_FOUND);
		}
		DailyBill retDailyBill = new DailyBill();
		Double borrow = 0d;
		Double loan = 0d;
		for (TradeInfo tradeInfoTmp : list) {
			if (MessageDef.TRADE_STATUS.SUCC == tradeInfoTmp.getStatus()) {
				if (MessageDef.TRADE_TYPE.RECHARGE == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getRecvUserId().equals(user_id)) {
						loan = DoubleUtil.add(loan, tradeInfoTmp.getRecvSumAmount());
					}
				}
				if (MessageDef.TRADE_TYPE.CHARGE == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getPayUserId().equals(user_id)) {
						borrow = DoubleUtil.add(borrow, tradeInfoTmp.getPayAmount());
					}
				}
				if (MessageDef.TRADE_TYPE.TRANSFER == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getPayUserId().equals(user_id)) {
						// if(MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER == tradeInfoTmp.getPayGetwayType()){
						borrow += tradeInfoTmp.getPayAmount();
						// }
					} else {
						loan = DoubleUtil.add(loan, tradeInfoTmp.getRecvSumAmount());
					}
				}
				if (MessageDef.TRADE_TYPE.TRANSFER_BANK == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getPayUserId().equals(user_id)) {
						borrow += tradeInfoTmp.getPayAmount();
					} else {
						loan = DoubleUtil.add(loan, tradeInfoTmp.getRecvSumAmount());
					}
				}
				if (MessageDef.TRADE_TYPE.CONSUMPTION == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getPayUserId().equals(user_id)) {
						borrow += tradeInfoTmp.getPayAmount();
					} else {
						loan = DoubleUtil.add(loan, tradeInfoTmp.getRecvSumAmount());
					}
				}
			}
		}
		retDailyBill.setBorrow(borrow);
		retDailyBill.setLoan(loan);
		return new ResponseRestEntity<DailyBill>(retDailyBill, HttpRestStatus.OK);
	}

	// 查询月账单
	@PreAuthorize("hasRole('R_TRADE_M_B_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query month Bill", notes = "")
	@RequestMapping(value = "/getMouthBill", method = RequestMethod.GET)
	public ResponseRestEntity<DailyBill> getMouthBill(@RequestParam(required = false) String user_id) {

		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
		if (user_id != null && !user_id.isEmpty()) {
			tradeInfo.setPayUserId(user_id);
			tradeInfo.setRecvUserId(user_id);
		} else {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOTEMPTY, "用户名编号用户名必须填写一个");
		}

		tradeInfo.setCreateTimeStartSer(DateUtil.convertDateToString(new Date(System.currentTimeMillis()), "YYYY-MM-01") + " 00:00:00");
		tradeInfo.setType(MessageDef.TRADE_TYPE.CONSUMPTION);

		List<TradeInfo> list = tradeInfoService.selectPageByUser(tradeInfo);

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<DailyBill>(HttpRestStatus.NOT_FOUND);
		}
		DailyBill retDailyBill = new DailyBill();
		Double borrow = 0d;
		Double loan = 0d;
		for (TradeInfo tradeInfoTmp : list) {
			if (MessageDef.TRADE_STATUS.SUCC == tradeInfoTmp.getStatus()) {
				if (MessageDef.TRADE_TYPE.RECHARGE == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getRecvUserId().equals(user_id)) {
						loan = DoubleUtil.add(loan, tradeInfoTmp.getRecvSumAmount());
					}
				}
				if (MessageDef.TRADE_TYPE.CHARGE == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getPayUserId().equals(user_id)) {
						borrow = DoubleUtil.add(borrow, tradeInfoTmp.getPayAmount());
					}
				}
				if (MessageDef.TRADE_TYPE.TRANSFER == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getPayUserId().equals(user_id)) {
						// if(MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER == tradeInfoTmp.getPayGetwayType()){
						borrow += tradeInfoTmp.getPayAmount();
						// }
					} else {
						loan = DoubleUtil.add(loan, tradeInfoTmp.getRecvSumAmount());
					}
				}
				if (MessageDef.TRADE_TYPE.TRANSFER_BANK == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getPayUserId().equals(user_id)) {
						borrow += tradeInfoTmp.getPayAmount();
					} else {
						loan = DoubleUtil.add(loan, tradeInfoTmp.getRecvSumAmount());
					}
				}
				if (MessageDef.TRADE_TYPE.CONSUMPTION == tradeInfoTmp.getType()) {
					if (tradeInfoTmp.getPayUserId().equals(user_id)) {
						borrow += tradeInfoTmp.getPayAmount();
					} else {
						loan = DoubleUtil.add(loan, tradeInfoTmp.getRecvSumAmount());
					}
				}
			}
		}
		retDailyBill.setBorrow(borrow);
		retDailyBill.setLoan(loan);
		return new ResponseRestEntity<DailyBill>(retDailyBill, HttpRestStatus.OK);
	}

	// 查询用户交易信息
	@PreAuthorize("hasRole('R_B_TRADE_I_Q')")
	@ApiOperation(value = "Query Buyer TradeInfo", notes = "")
	@RequestMapping(value = "/consumer", method = RequestMethod.POST)
	public ResponseRestEntity<List<TradeInfo>> selectBuyerPage(@RequestParam(required = false) String trade_seq, @RequestParam(required = false) String idNumber, @RequestParam(required = false) String type,
			@RequestParam(required = false) String status, @RequestParam(required = false) String createTimeEnd, @RequestParam(required = false) String parentTradeSeq, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {

		idNumber = CommonFun.getRelVid(idNumber);
		TradeInfo tradeInfo = new TradeInfo();

		// 输入idNumber查询
		if ((trade_seq == null || "".equals(trade_seq)) && (idNumber == null || "".equals(idNumber)) && (type == null || "".equals(type)) && (status == null || "".equals(status))
				&& (createTimeEnd == null || "".equals(createTimeEnd)) && (parentTradeSeq == null || "".equals(parentTradeSeq))) {
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
		}
		if (idNumber != "") {

			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(idNumber);
			if (consumerUserClap != null) {
				tradeInfo.setPayUserId(consumerUserClap.getUserId());
				tradeInfo.setRecvUserId(consumerUserClap.getUserId());
			} else {
				return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
			}
		}

		try {
			if (type == null || "".equals(type)) {

			} else {
				tradeInfo.setType(Integer.valueOf(type));
			}

		} catch (Exception e) {
			log.error("", e);
		}

		try {
			if (status == null || "".equals(status)) {

			} else {
				tradeInfo.setStatus(Integer.valueOf(status));
			}
		} catch (Exception e) {
			log.error("", e);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// if (createTimeStart == null || "".equals(createTimeStart)) {
		// tradeInfo.setCreateTimeStartSer(sdf.format(new Date()));
		// } else {
		// tradeInfo.setCreateTimeStartSer(createTimeStart);
		// }

		if (createTimeEnd == null || "".equals(createTimeEnd)) {
			tradeInfo.setCreateTimeEndSer(sdf.format(new Date()));
		} else {
			tradeInfo.setCreateTimeEndSer(createTimeEnd);
		}
		tradeInfo.setTradeSeq(trade_seq);
		tradeInfo.setDeleteFlag(PageHelperUtil.DELETE_NO);

		List<TradeInfo> list = new ArrayList<TradeInfo>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = tradeInfoService.selectPageByUser(tradeInfo);

		} else {
			list = tradeInfoService.selectPageByUser(tradeInfo);
		}

		int count = tradeInfoService.countByUser(tradeInfo);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
		}
		List<TradeInfo> listNew = new ArrayList<TradeInfo>();
		for (TradeInfo bean : list) {
			PayGateway payGateway = payGatewayService.selectByPrimaryKey(bean.getRecvGatewayId());
			if (payGateway != null) {
				bean.setRecvGatewayName(payGateway.getName());
			}
			if(parentTradeSeq == null || "".equals(parentTradeSeq)){
			listNew.add(bean);
			 }
			else{
				if(bean.getParentTradeSeq().equals(parentTradeSeq)){
					listNew.add(bean);
					return new ResponseRestEntity<List<TradeInfo>>(listNew, HttpRestStatus.OK, count, count);
				}
					
			}
		}
		return new ResponseRestEntity<List<TradeInfo>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询商户交易信息
	@PreAuthorize("hasRole('R_S_TRADE_I_Q')")
	@ApiOperation(value = "Query TradeInfo，Support paging", notes = "")
	@RequestMapping(value = "/merchant", method = RequestMethod.POST)
	public ResponseRestEntity<List<TradeInfo>> selectSellerPage(@RequestParam(required = false) String trade_seq, @RequestParam(required = false) String clapStoreNo, @RequestParam(required = false) String type,
			@RequestParam(required = false) String status, @RequestParam(required = false) String createTimeEnd, @RequestParam(required = false) String parentTradeSeq, @RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		TradeInfo tradeInfo = new TradeInfo();
		// 输入idNumber查询
		if ((trade_seq == null || "".equals(trade_seq)) && (clapStoreNo == null || "".equals(clapStoreNo)) && (type == null || "".equals(type)) && (status == null || "".equals(status))
				&& (createTimeEnd == null || "".equals(createTimeEnd)) && (parentTradeSeq == null || "".equals(parentTradeSeq))) {
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
		}
		tradeInfo.setTradeSeq(trade_seq);

		if (clapStoreNo != "") {
			MerchantUser merchantUser = merchantUserService.selectByIdNumber(CommonFun.getRelVid(clapStoreNo));//clapStoreNo=IdNumber

			if (merchantUser != null) {
				tradeInfo.setPayUserId(merchantUser.getUserId());
				tradeInfo.setRecvUserId(merchantUser.getUserId());
			} else {
				return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
			}
		}

		try {
			if (type == null || "".equals(type)) {

			} else {
				tradeInfo.setType(Integer.valueOf(type));
			}

		} catch (Exception e) {
			log.error("", e);
		}

		try {
			if (status == null || "".equals(status)) {

			} else {
				tradeInfo.setStatus(Integer.valueOf(status));
			}
		} catch (Exception e) {
			log.error("", e);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// if (createTimeStart == null || "".equals(createTimeStart)) {
		// tradeInfo.setCreateTimeStartSer(sdf.format(new Date()));
		// } else {
		// tradeInfo.setCreateTimeStartSer(createTimeStart);
		// }

		if (createTimeEnd == null || "".equals(createTimeEnd)) {
			tradeInfo.setCreateTimeEndSer(sdf.format(new Date()));
		} else {
			tradeInfo.setCreateTimeEndSer(createTimeEnd);
		}

		tradeInfo.setDeleteFlag(PageHelperUtil.DELETE_NO);

		List<TradeInfo> list = new ArrayList<TradeInfo>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = tradeInfoService.selectPageByUser(tradeInfo);

		} else {
			list = tradeInfoService.selectPageByUser(tradeInfo);
		}

		int count = tradeInfoService.countByUser(tradeInfo);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<TradeInfo>>(new ArrayList<TradeInfo>(), HttpRestStatus.NOT_FOUND);
		}
		List<TradeInfo> listNew = new ArrayList<TradeInfo>();
		for (TradeInfo bean : list) {
			PayGateway payGateway = payGatewayService.selectByPrimaryKey(bean.getRecvGatewayId());
			if (payGateway != null) {
				bean.setRecvGatewayName(payGateway.getName());
			}
			if(parentTradeSeq == null || "".equals(parentTradeSeq)){
				listNew.add(bean);
				 }
				else{
					if(bean.getParentTradeSeq().equals(parentTradeSeq)){
						listNew.add(bean);
						return new ResponseRestEntity<List<TradeInfo>>(listNew, HttpRestStatus.OK, count, count);
					}
						
				}
			}
		
		return new ResponseRestEntity<List<TradeInfo>>(listNew, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_TRADE_I_Q') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	@RequestMapping("/findTradeInfoByTradeNo")
	public ResponseRestEntity<TradeInfo> selectTradeInfoByOrderNo(@RequestParam(required = true) String orderNo) {
		TradeInfo info = new TradeInfo();
		if (null == orderNo || "".equals(orderNo)) {
			return new ResponseRestEntity<TradeInfo>(info, HttpRestStatus.NOT_FOUND);
		}
		TradeInfo infoTmp = new TradeInfo();
		infoTmp.setMerchantOrderNo(orderNo);
		Date now = Calendar.getInstance().getTime();
		Date yesterday = DateUtils.addDays(now, -1);
		infoTmp.setCreateTimeStartSer(DateUtil.convertDateToFormatString(yesterday));
		infoTmp.setCreateTimeEndSer(DateUtil.convertDateToFormatString(now));
		List<TradeInfo> list = tradeInfoService.selectbyOrderNoInDay(infoTmp);
		if (list != null && list.size() == 1) {
			info = list.get(0);
		}
		if (null != info) {
			return new ResponseRestEntity<TradeInfo>(info, HttpRestStatus.OK);
		}

		return new ResponseRestEntity<TradeInfo>(info, HttpRestStatus.NOT_FOUND);
	}

	@PreAuthorize("hasRole('CONSUMER') or hasRole('MERCHANT')")
	@RequestMapping("/getOrderNo")
	public ResponseRestEntity<String> getOrderNo() {
		String retStr = RedisUtil.get_ORDER_NO_CHARGE();
		if (retStr == null) {
			return new ResponseRestEntity<String>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<String>(retStr, HttpRestStatus.OK);
	}

}