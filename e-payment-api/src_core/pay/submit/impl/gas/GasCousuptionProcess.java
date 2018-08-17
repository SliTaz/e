package org.e.payment.core.pay.submit.impl.gas;


import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.*;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.e.payment.core.pay.submit.impl.AbsSubmitPayProcess;
import org.e.payment.core.pay.submit.impl.reverse.ConsumptionReservseProcess;
import org.e.payment.core.pay.submit.vo.ConsuptionReservseResponse;
import org.e.payment.core.pay.submit.vo.ConsuptionReverseRequest;
import org.e.payment.core.pay.submit.vo.SubmitTradeConsumptionAppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * 交易 revserse
 * 1. 根据tradeSeq找到 该对应的 tradeinfo信息
 * 2. 根据coupon判断是否为clap类型消费
 * 3. 1) 如果是非clap类型消费,新增tradeinfo记录 状态为 13,修改之前状态为 已退款,付款人,补款,商家减款
 *    2)如果是clap类型消费,新增tradeinfo记录 状态为13,修改之前状态为 已退款,付款人付款人补款,商家减款,返回券的状态,可用
 */
public class GasCousuptionProcess  extends AbsSubmitPayProcess {
    private static final Logger log = LoggerFactory.getLogger(GasCousuptionProcess.class);

    @Override
    public ResponseRestEntity<?> payProcess(Object request) {

        TradeInfo tradeInfo = null;
        String recvUserId = null;
        String recvUserName = null;
        String payEmployeeUserId=null;
        String payEmployeeUserName=null;
        String payUserId = null;
        String payUserName = null;
        String payOfficeId = null;
        String couponId = null;

        MerchantUser mainPayUser=null;
        String payMainUserId = null;
        String payMainUserName = null;
        Date now = new Date(System.currentTimeMillis());




        //交易提交类型
        validateRequestClass(request != null && request instanceof ConsuptionReverseRequest);
        if (isErrorResponse()) {
            return response;
        }

        ConsuptionReverseRequest consuptionReverseRequest = (ConsuptionReverseRequest) request;
        tradeInfo = tradeInfoService.getTradInfoByTradeSeq(consuptionReverseRequest.getTradeSeq());

        if(tradeInfo == null || MessageDef.TRADE_STATUS.SUCC !=  tradeInfo.getStatus())
            this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_TRADE_NOTEXIST);

        //校验重复提交 Add by Wang 2018-07-13
        validateExistOrderNo(consuptionReverseRequest.getOrderNo(), now);
        if (isErrorResponse()) {
            return response;
        }

        //用户校验
        ConsumerUser receiveUser = validatePayUserConsumer(consuptionReverseRequest.getReceiveUserId());

        ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(consuptionReverseRequest.getReceiveUserId());
        recvUserId = receiveUser.getUserId();
        recvUserName =  receiveUser.getUserName();
        if (isErrorResponse()) {
            return response;
        }

        MerchantUser payUser = validatePayUserMerchant(consuptionReverseRequest.getPayUserId());//
        if (isErrorResponse()) {
            return response;
        }

        MerchantEmployee payEmployee = null;
        if(!isProviderWithNoRif(payUser)){
            //商户员工校验
            payEmployee = validateRecvUserEmployee(consuptionReverseRequest.getPayEmployeeId());//员工反款
            payUserId = payEmployee.getUserId();
            payUserName=CommonFun.getMerchantUserName(payUser);
            payEmployeeUserName = CommonFun.getMerchantEmployeeUserName(payEmployee);
            if (isErrorResponse()) {
                return response;
            }

            //商户员工对应的 provider
            if(payEmployee!=null){
                payEmployeeUserId = payEmployee.getEmployeeUserId();
                payOfficeId = payEmployee.getUserId();
            }
        }else{
            payUserId = consuptionReverseRequest.getPayUserId();
            payUserName=CommonFun.getMerchantUserName(payUser);
        }


        if(StringUtils.isNotEmpty(payUser.getHeadOfficeId())){
            mainPayUser = validateRecvUserMerchant(payUser.getHeadOfficeId());//
            if (isErrorResponse()) {
                return response;
            }
            payMainUserId = mainPayUser.getUserId();
            payMainUserName = CommonFun.getMerchantUserName(mainPayUser);
        }


        // payAppId
        validatePayAppId(consuptionReverseRequest.getPayAppId());
        if (isErrorResponse()) {
            return response;
        }
        PayGateway payGateway = validatePayGateway(consuptionReverseRequest.getPayAppId(), MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER);
        if (isErrorResponse()) {
            return response;
        }

        // tradeType
        validateTrade(consuptionReverseRequest.getTradeType(), MessageDef.TRADE_TYPE.REFUND);
        if (isErrorResponse()) {
            return response;
        }

        // amount
        validatePayAmount(consuptionReverseRequest.getPayBackAmount());
        if (isErrorResponse()) {
            return response;
        }

        //buyer 支付密码校验
        validatePayPassword(recvUserId, receiveUser.getPayPassword(), consuptionReverseRequest.getConsumerPayPassword());
        if (isErrorResponse()) {
            return response;
        }

        //关联子账单
        TradeInfo submitTradeInfo = new TradeInfo();
        submitTradeInfo.setTradeSeq(IDGenerate.generateCommTwo(IDGenerate.TRAD_SEQ));
        submitTradeInfo.setParentTradeSeq(tradeInfo.getTradeSeq());
        submitTradeInfo.setPayAppId(consuptionReverseRequest.getPayAppId());
        submitTradeInfo.setPayGatewayId(payGateway.getPayGatewayId());// 余额支付
        submitTradeInfo.setType(MessageDef.TRADE_TYPE.REFUND);
        submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.REFUND_succ);
        submitTradeInfo.setErrorCode(null);
        submitTradeInfo.setHaveRefund(MessageDef.TRADE_HAVE_REFUND.NO);
        submitTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.CLOSE);
        // submitTradeInfo.setConsumptionName(localeMessageSourceService.getMessage("epaymentpay.pay.type.payee"));
        submitTradeInfo.setConsumptionName(null);
        submitTradeInfo.setMerchantOrderNo(consuptionReverseRequest.getOrderNo());
        submitTradeInfo.setPayUserId(payUserId);
        submitTradeInfo.setPayUserName(payUserName);
        submitTradeInfo.setPayEmployeeUserId(payEmployeeUserId);
        submitTradeInfo.setPayEmployeeUserName(payEmployeeUserName);
        submitTradeInfo.setPayBankId(null);
        submitTradeInfo.setPayBankName(null);
        submitTradeInfo.setPayBankCardNo(null);
        submitTradeInfo.setPayBankCardHolerName(null);
        submitTradeInfo.setPayGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
        submitTradeInfo.setPayBankType(null);
        submitTradeInfo.setPayBankOrderNo(null);
        submitTradeInfo.setPayAmount(Double.valueOf(consuptionReverseRequest.getPayBackAmount()));
        submitTradeInfo.setPayFee(0d);
        submitTradeInfo.setPaySumAmount(DoubleUtil.add(submitTradeInfo.getPayAmount(), submitTradeInfo.getPayFee()));
        submitTradeInfo.setPayStartMoney(null);
        submitTradeInfo.setPayEndMoney(null);
        submitTradeInfo.setPayBorrowLoanFlag(MessageDef.BORROW_LOAN.LOAN);
        submitTradeInfo.setRecvUserId(recvUserId);
        submitTradeInfo.setRecvUserName(recvUserName);
        submitTradeInfo.setRecvEmployeeUserId(payEmployeeUserId);
        submitTradeInfo.setRecvEmployeeUserName(payEmployeeUserName);
        submitTradeInfo.setPayMainUserId(payMainUserId);
        submitTradeInfo.setPayMainUserName(payMainUserName);
        submitTradeInfo.setRecvGatewayId(payGateway.getPayGatewayId());// 余额支付
        submitTradeInfo.setRecvBankId(null);
        submitTradeInfo.setRecvBankName(null);
        submitTradeInfo.setRecvBankCardNo(null);
        submitTradeInfo.setRecvBankCardHolerName(null);
        submitTradeInfo.setRecvGetwayType(Integer.valueOf(payGateway.getPayGatewayTypeId()));
        submitTradeInfo.setRecvBankType(null);
        submitTradeInfo.setRecvBankOrderNo(null);
        submitTradeInfo.setRecvAmount(Double.valueOf(consuptionReverseRequest.getPayBackAmount()));
        submitTradeInfo.setRecvFee(0d);
        submitTradeInfo.setRecvSumAmount(DoubleUtil.add(submitTradeInfo.getRecvAmount(), submitTradeInfo.getRecvFee()));
        submitTradeInfo.setRecvStartMoney(null);
        submitTradeInfo.setRecvEndMoney(null);
        submitTradeInfo.setRecvBorrowLoanFlag(MessageDef.BORROW_LOAN.BORROW);
        submitTradeInfo.setCallbackUrl(null);
        submitTradeInfo.setCreateTime(now);
        submitTradeInfo.setPayTime(null);
        submitTradeInfo.setEndTime(null);
        submitTradeInfo.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
        submitTradeInfo.setRemark(null);
        submitTradeInfo.setConsumerCouponId(tradeInfo.getConsumerCouponId());

        processPayUserAccount(submitTradeInfo);
        if (isErrorResponse()) {
            rollbackUserAccount(submitTradeInfo);
            return response;
        }

        processRecvUserAccount(submitTradeInfo);
        if (isErrorResponse()) {
            rollbackUserAccount(submitTradeInfo);
            return response;
        }

        submitTradeInfo.setStatus(MessageDef.TRADE_STATUS.SUCC);
        submitTradeInfo.setEndTime(Calendar.getInstance().getTime());

        try {
            //修改之前账单为已退款
            boolean isClap = isProviderWithNoRif(payUser) ? false : validateIsClapStore(payEmployee);
            tradeInfoService.cousuptionReversed(tradeInfo,submitTradeInfo,isClap,consumerUserClap,tradeInfo.getConsumerCouponId());
        } catch (Exception e) {
            MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "ConsumptionReservseProcess tradeInfoService.markedHasRefundStatus", JSONObject.toJSONString(submitTradeInfo)));
            log.error("", e);
            rollbackUserAccount(submitTradeInfo);
            return new ResponseRestEntity<SubmitTradeConsumptionAppResponse>(HttpRestStatus.PAY_ERROR, localeMessageSourceService.getMessage("epaymentpay.pay.submit.exception"));
        }

        sendToRabbitmq(submitTradeInfo);

        ConsuptionReservseResponse consuptionReservseResponse = new ConsuptionReservseResponse();
        consuptionReservseResponse.setTradeSeq(submitTradeInfo.getTradeSeq());
        consuptionReservseResponse.setCreateTime(DateUtil.convertDateToString(submitTradeInfo.getCreateTime(), DateUtil.DATE_FORMAT_TWENTY_FOUR));
        return new ResponseRestEntity<ConsuptionReservseResponse>(consuptionReservseResponse, HttpRestStatus.OK);
    }




    private boolean isProviderWithNoRif(MerchantUser payUser) {
        return null == payUser.getRif() || "".equals(payUser.getRif());
    }

    protected MerchantUser validatePayUserMerchant(String payUserId) {
        MerchantUser payUser = null;
        if (payUserId != null && !payUserId.isEmpty()) {
            payUser = merchantUserService.selectByPrimaryKey(payUserId);
        }

        if (payUser == null) {
            this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_RECV_USER_NOEXIST, localeMessageSourceService.getMessage("epaymentpay.recver.notexist"));
            return payUser;
        }
        if (MessageDef.STATUS.ENABLE_INT != payUser.getStatus()) {
            this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_DISABLE, localeMessageSourceService.getMessage("epaymentpay.recver.disable"));
            return payUser;
        }

        if (MessageDef.LOCKED.UNLOCKED != payUser.getIsLocked()) {
            this.response = new ResponseRestEntity<>(HttpRestStatus.PAY_PAY_USER_LOCKED, localeMessageSourceService.getMessage("epaymentpay.recver.locked"));
            return payUser;
        }
        return payUser;
    }
}
