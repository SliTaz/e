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
import com.zbensoft.e.payment.api.service.api.BankCardTypeService;
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.api.service.api.MerchantUserBankCardService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.BankCardType;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantUserBankCard")
@RestController
public class MerchantUserBankCardController {
	@Autowired
	MerchantUserBankCardService merchantUserBankCardService;
	
	@Autowired
	BankInfoService bankInfoService;
	@Autowired
	MerchantUserService merchantUserService;
	@Autowired
	BankCardTypeService bankCardTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询商户银行卡，支持分页
	@PreAuthorize("hasRole('R_SELLER_B_C_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query MerchantUserBankCard，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantUserBankCard>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String idNumber, @RequestParam(required = false) String holerName, 
			@RequestParam(required = false) String userId, @RequestParam(required = false) String bankId,
			@RequestParam(required = false) String cardNo,@RequestParam(required = false) String sellerIdNumber,  @RequestParam(required = false) Integer type,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		sellerIdNumber = CommonFun.getRelVid(sellerIdNumber);
		MerchantUserBankCard merchantUserBankCard = new MerchantUserBankCard();
		// 必须输入一个进行查询
				if ((idNumber == null || "".equals(idNumber)) && (id == null || "".equals(id)) && (userId == null || "".equals(userId))
						&& (holerName == null || "".equals(holerName))&& (bankId == null || "".equals(bankId))&& (sellerIdNumber == null || "".equals(sellerIdNumber))) {
					return new ResponseRestEntity<List<MerchantUserBankCard>>(new ArrayList<MerchantUserBankCard>(), HttpRestStatus.NOT_FOUND);
				}
				
				if (sellerIdNumber == null || "".equals(sellerIdNumber)) {
					merchantUserBankCard.setUserId(userId);
				} else {
					MerchantUser merchantUser = merchantUserService.selectByIdNumber(sellerIdNumber);
					if (merchantUser == null) {
						return new ResponseRestEntity<List<MerchantUserBankCard>>(new ArrayList<MerchantUserBankCard>(), HttpRestStatus.NOT_FOUND);
					} else {
						if (userId == null || "".equals(userId)) {
							merchantUserBankCard.setUserId(merchantUser.getUserId());
						} else {
							if (userId.equals(merchantUser.getUserId())) {
								merchantUserBankCard.setUserId(userId);
							} else {
								return new ResponseRestEntity<List<MerchantUserBankCard>>(new ArrayList<MerchantUserBankCard>(), HttpRestStatus.NOT_FOUND);
							}
						}
					}

				}
				
		merchantUserBankCard.setUserId(userId);
		merchantUserBankCard.setBankBindId(id);
		merchantUserBankCard.setIdNumber(idNumber);
		merchantUserBankCard.setHolerName(holerName);
		merchantUserBankCard.setBankId(bankId);
		merchantUserBankCard.setCardNo(cardNo);
		merchantUserBankCard.setType(type);
		merchantUserBankCard.setDeleteFlag(0);
		List<MerchantUserBankCard> list = new ArrayList<MerchantUserBankCard>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantUserBankCardService.selectPage(merchantUserBankCard);

		} else {
			list = merchantUserBankCardService.selectPage(merchantUserBankCard);
		}

		int count = merchantUserBankCardService.count(merchantUserBankCard);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantUserBankCard>>(new ArrayList<MerchantUserBankCard>(),
					HttpRestStatus.NOT_FOUND);
		}
		List<MerchantUserBankCard> listNew = new ArrayList<MerchantUserBankCard>();
		for(MerchantUserBankCard bean:list){
			BankInfo bankInfo = bankInfoService.selectByPrimaryKey(bean.getBankId());
			BankCardType bankCardType = bankCardTypeService.selectByPrimaryKey(bean.getType());
			if(bankInfo!=null){
				bean.setBankName(bankInfo.getName());
			}
			if(bankCardType!=null){
				bean.setBankCardTypeName(bankCardType.getName());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<MerchantUserBankCard>>(listNew, HttpRestStatus.OK, count, count);
		
		
	}

	// 查询商户银行卡
	@PreAuthorize("hasRole('R_SELLER_B_C_Q') or hasRole('MERCHANT')")
	@ApiOperation(value = "Query MerchantUserBankCard", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantUserBankCard> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantUserBankCard merchantUserBankCard = merchantUserBankCardService.selectByPrimaryKey(id);
		if (merchantUserBankCard == null) {
			return new ResponseRestEntity<MerchantUserBankCard>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantUserBankCard>(merchantUserBankCard, HttpRestStatus.OK);
	}

	// 新增商户银行卡
	@PreAuthorize("hasRole('R_SELLER_B_C_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Add MerchantUserBankCard", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody MerchantUserBankCard merchantUserBankCard,
			BindingResult result, UriComponentsBuilder ucBuilder) {
		merchantUserBankCard.setIdNumber(CommonFun.getRelVid(merchantUserBankCard.getIdNumber()));
		MerchantUserBankCard bankCard = merchantUserBankCardService.selectByUserIdCardNo(merchantUserBankCard);
		if(bankCard!=null){
			bankCard.setDeleteFlag(0);
			bankCard.setBankId(merchantUserBankCard.getBankId());
			bankCard.setHolerName(merchantUserBankCard.getHolerName());
			bankCard.setIdNumber(merchantUserBankCard.getIdNumber());
			merchantUserBankCardService.updateByPrimaryKey(bankCard);
		}
		else{
		merchantUserBankCard.setBankBindId(IDGenerate.generateCommOne(IDGenerate.MERCHANT_USER_BANK_CARD));
		merchantUserBankCard.setDeleteFlag(0);
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantUserBankCardService.insert(merchantUserBankCard);
		}
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantUserBankCard,CommonLogImpl.MERCHANT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/merchantUserBankCard/{id}")
				.buildAndExpand(merchantUserBankCard.getBankBindId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改商户银行卡信息
	@PreAuthorize("hasRole('R_SELLER_B_C_E')")
	@ApiOperation(value = "Edit MerchantUserBankCard", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUserBankCard> update(@PathVariable("id") String id,
			@Valid @RequestBody MerchantUserBankCard merchantUserBankCard, BindingResult result) {

		merchantUserBankCard.setIdNumber(CommonFun.getRelVid(merchantUserBankCard.getIdNumber()));
		MerchantUserBankCard currentMerchantUserBankCard = merchantUserBankCardService.selectByPrimaryKey(id);

		if (currentMerchantUserBankCard == null) {
			return new ResponseRestEntity<MerchantUserBankCard>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentMerchantUserBankCard.setUserId(merchantUserBankCard.getUserId());
		currentMerchantUserBankCard.setIdNumber(merchantUserBankCard.getIdNumber());
		currentMerchantUserBankCard.setHolerName(merchantUserBankCard.getHolerName());
		currentMerchantUserBankCard.setBankId(merchantUserBankCard.getBankId());
		currentMerchantUserBankCard.setCardNo(merchantUserBankCard.getCardNo());
		currentMerchantUserBankCard.setType(merchantUserBankCard.getType());
		currentMerchantUserBankCard.setMobileNo(merchantUserBankCard.getMobileNo());
		currentMerchantUserBankCard.setRemark(merchantUserBankCard.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<MerchantUserBankCard>(currentMerchantUserBankCard,
					HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		merchantUserBankCardService.updateByPrimaryKey(currentMerchantUserBankCard);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantUserBankCard,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUserBankCard>(currentMerchantUserBankCard, HttpRestStatus.OK,
				localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分商户银行卡信息
	@PreAuthorize("hasRole('R_SELLER_B_C_E')")
	@ApiOperation(value = "Edit Part MerchantUserBankCard", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantUserBankCard> updateSelective(@PathVariable("id") String id,
			@RequestBody MerchantUserBankCard merchantUserBankCard) {

		merchantUserBankCard.setIdNumber(CommonFun.getRelVid(merchantUserBankCard.getIdNumber()));
		MerchantUserBankCard currentMerchantUserBankCard = merchantUserBankCardService.selectByPrimaryKey(id);

		if (currentMerchantUserBankCard == null) {
			return new ResponseRestEntity<MerchantUserBankCard>(HttpRestStatus.NOT_FOUND);
		}
		merchantUserBankCard.setBankBindId(id);
		currentMerchantUserBankCard.setUserId(merchantUserBankCard.getUserId());
		currentMerchantUserBankCard.setIdNumber(merchantUserBankCard.getIdNumber());
		currentMerchantUserBankCard.setHolerName(merchantUserBankCard.getHolerName());
		currentMerchantUserBankCard.setBankId(merchantUserBankCard.getBankId());
		currentMerchantUserBankCard.setCardNo(merchantUserBankCard.getCardNo());
		currentMerchantUserBankCard.setType(merchantUserBankCard.getType());
		currentMerchantUserBankCard.setMobileNo(merchantUserBankCard.getMobileNo());
		currentMerchantUserBankCard.setRemark(merchantUserBankCard.getRemark());
		merchantUserBankCardService.updateByPrimaryKeySelective(currentMerchantUserBankCard);// ?
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentMerchantUserBankCard,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUserBankCard>(currentMerchantUserBankCard, HttpRestStatus.OK);
	}

	// 删除指定商户银行卡
	@PreAuthorize("hasRole('R_SELLER_B_C_E') or hasRole('MERCHANT')")
	@ApiOperation(value = "Delete MerchantUserBankCard", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantUserBankCard> delete(@PathVariable("id") String id) {

		MerchantUserBankCard merchantUserBankCard = merchantUserBankCardService.selectByPrimaryKey(id);
		if (merchantUserBankCard == null) {
			return new ResponseRestEntity<MerchantUserBankCard>(HttpRestStatus.NOT_FOUND);
		}

		merchantUserBankCard.setDeleteFlag(1);
		merchantUserBankCardService.updateByPrimaryKeySelective(merchantUserBankCard);
		//删除日志开始
		MerchantUserBankCard merchant = new MerchantUserBankCard();
		merchant.setBankBindId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
		return new ResponseRestEntity<MerchantUserBankCard>(HttpRestStatus.OK);
	}
	@PreAuthorize("hasRole('R_SELLER_B_C_E')")
	@ApiOperation(value = "Query MerchantUserBankCard by userId", notes = "")
	@RequestMapping(value = "/userid/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<MerchantUserBankCard>> selectByUserId(@PathVariable("userId") String userId) {
		List<MerchantUserBankCard> merchantUserBankCardList = merchantUserBankCardService.selectByUserId(userId);
		if (merchantUserBankCardList == null) {
			merchantUserBankCardList = new ArrayList<>();
		}
		return new ResponseRestEntity<List<MerchantUserBankCard>>(merchantUserBankCardList, HttpRestStatus.OK);
	}

}