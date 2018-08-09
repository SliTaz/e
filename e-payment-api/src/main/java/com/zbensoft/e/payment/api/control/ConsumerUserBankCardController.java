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
import com.zbensoft.e.payment.api.service.api.ConsumerUserBankCardService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.db.domain.BankCardType;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerUserBankCard")
@RestController
public class ConsumerUserBankCardController {
	@Autowired
	ConsumerUserBankCardService consumerUserBankCardService;
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Autowired
	BankInfoService bankInfoService;
	@Autowired
	BankCardTypeService bankCardTypeService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询Clap卡，支持分页
	@PreAuthorize("hasRole('R_BUYER_B_C_Q') or hasRole('CONSUMER')")
	@ApiOperation(value = "Query ConsumerUserBankCard，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerUserBankCard>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String idNumber,@RequestParam(required = false) String buyerIdNumber, @RequestParam(required = false) String holerName, 
			@RequestParam(required = false) String userId, @RequestParam(required = false) String bankId,
			@RequestParam(required = false) String cardNo, @RequestParam(required = false) Integer type,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		buyerIdNumber = CommonFun.getRelVid(buyerIdNumber);
		ConsumerUserBankCard consumerUserBankCard = new ConsumerUserBankCard();
		// 必须输入一个进行查询
		if ((idNumber == null || "".equals(idNumber)) && (id == null || "".equals(id)) && (userId == null || "".equals(userId))
				&& (holerName == null || "".equals(holerName))&& (bankId == null || "".equals(bankId))&& (buyerIdNumber == null || "".equals(buyerIdNumber))) {
			return new ResponseRestEntity<List<ConsumerUserBankCard>>(new ArrayList<ConsumerUserBankCard>(), HttpRestStatus.NOT_FOUND);
		}
		if (buyerIdNumber == null || "".equals(buyerIdNumber)) {
			consumerUserBankCard.setUserId(userId);
		} else {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(buyerIdNumber);

 			if (consumerUserClap == null) {
				return new ResponseRestEntity<List<ConsumerUserBankCard>>(new ArrayList<ConsumerUserBankCard>(),
						HttpRestStatus.NOT_FOUND);

			} else {
				if (userId == null || "".equals(userId)) {
					consumerUserClap.setUserId(consumerUserClap.getUserId());
				} else {
					if (userId.equals(consumerUserClap.getUserId())) {
						consumerUserClap.setUserId(userId);
					} else {
						return new ResponseRestEntity<List<ConsumerUserBankCard>>(new ArrayList<ConsumerUserBankCard>(),
								HttpRestStatus.NOT_FOUND);
					}
				}
			}
		}
		
		consumerUserBankCard.setUserId(userId);
		consumerUserBankCard.setBankBindId(id);
		consumerUserBankCard.setIdNumber(idNumber);
		consumerUserBankCard.setHolerName(holerName);
		consumerUserBankCard.setBankId(bankId);
		consumerUserBankCard.setCardNo(cardNo);
	    consumerUserBankCard.setType(type);
	    consumerUserBankCard.setDeleteFlag(0);

		List<ConsumerUserBankCard> list = new ArrayList<ConsumerUserBankCard>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerUserBankCardService.selectPage(consumerUserBankCard);

		} else {
			list = consumerUserBankCardService.selectPage(consumerUserBankCard);
		}

		int count = consumerUserBankCardService.count(consumerUserBankCard);
		// 分页 end

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerUserBankCard>>(new ArrayList<ConsumerUserBankCard>(),
					HttpRestStatus.NOT_FOUND);
		}
		
		List<ConsumerUserBankCard> listNew = new ArrayList<ConsumerUserBankCard>();
		for(ConsumerUserBankCard bean:list){
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
		return new ResponseRestEntity<List<ConsumerUserBankCard>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询Clap卡
	@PreAuthorize("hasRole('R_BUYER_B_C_Q') or hasRole('CONSUMER')")
	@ApiOperation(value = "Query ConsumerUserBankCard", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerUserBankCard> selectByPrimaryKey(@PathVariable("id") String id) {
		ConsumerUserBankCard consumerUserBankCard = consumerUserBankCardService.selectByPrimaryKey(id);
		if (consumerUserBankCard == null) {
			return new ResponseRestEntity<ConsumerUserBankCard>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerUserBankCard>(consumerUserBankCard, HttpRestStatus.OK);
	}

	// 新增Clap卡
	@PreAuthorize("hasRole('R_BUYER_B_C_E')")
	@ApiOperation(value = "Add ConsumerUserBankCard", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody ConsumerUserBankCard consumerUserBankCard,
			BindingResult result, UriComponentsBuilder ucBuilder) {
	/*	ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(consumerUserBankCard.getIdNumber());
		if (consumerUserClap==null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONSUMER_NOT_FOUND,
					localeMessageSourceService.getMessage("common.create.bank.message"));
		}*/
		//consumerUserBankCard.setUserId(consumerUserClap.getUserId());

		consumerUserBankCard.setIdNumber(CommonFun.getRelVid(consumerUserBankCard.getIdNumber()));
		ConsumerUserBankCard bankCard = consumerUserBankCardService.selectByCardNo(consumerUserBankCard.getCardNo());
		if(bankCard!=null){
			bankCard.setDeleteFlag(0);
			bankCard.setBankId(consumerUserBankCard.getBankId());
			bankCard.setHolerName(consumerUserBankCard.getHolerName());
			bankCard.setIdNumber(consumerUserBankCard.getIdNumber());
			consumerUserBankCardService.updateByPrimaryKey(bankCard);
		}
		else{
		consumerUserBankCard.setBankBindId(IDGenerate.generateCommOne(IDGenerate.CONSUMER_USER_BANK_CARD));
		consumerUserBankCard.setDeleteFlag(0);
     
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		consumerUserBankCardService.insert(consumerUserBankCard);
		}
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerUserBankCard,CommonLogImpl.CONSUMER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/consumerUserBankCard/{id}")
				.buildAndExpand(consumerUserBankCard.getBankBindId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改Clap卡信息
	@PreAuthorize("hasRole('R_BUYER_B_C_E')")
	@ApiOperation(value = "Edit ConsumerUserBankCard", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUserBankCard> update(@PathVariable("id") String id,
			@Valid @RequestBody ConsumerUserBankCard consumerUserBankCard, BindingResult result) {

		consumerUserBankCard.setIdNumber(CommonFun.getRelVid(consumerUserBankCard.getIdNumber()));
		ConsumerUserBankCard currentConsumerUserBankCard = consumerUserBankCardService.selectByPrimaryKey(id);

		if (currentConsumerUserBankCard == null) {
			return new ResponseRestEntity<ConsumerUserBankCard>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentConsumerUserBankCard.setUserId(consumerUserBankCard.getUserId());
		currentConsumerUserBankCard.setIdNumber(consumerUserBankCard.getIdNumber());
		currentConsumerUserBankCard.setHolerName(consumerUserBankCard.getHolerName());
		currentConsumerUserBankCard.setBankId(consumerUserBankCard.getBankId());
		currentConsumerUserBankCard.setCardNo(consumerUserBankCard.getCardNo());
		currentConsumerUserBankCard.setType(consumerUserBankCard.getType());
		currentConsumerUserBankCard.setMobileNo(consumerUserBankCard.getMobileNo());
		currentConsumerUserBankCard.setRemark(consumerUserBankCard.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<ConsumerUserBankCard>(currentConsumerUserBankCard,
					HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		consumerUserBankCardService.updateByPrimaryKey(currentConsumerUserBankCard);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerUserBankCard,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUserBankCard>(currentConsumerUserBankCard, HttpRestStatus.OK,
				localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分Clap卡信息
	@PreAuthorize("hasRole('R_BUYER_B_C_E')")
	@ApiOperation(value = "Edit Part ConsumerUserBankCard", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerUserBankCard> updateSelective(@PathVariable("id") String id,
			@RequestBody ConsumerUserBankCard consumerUserBankCard) {

		consumerUserBankCard.setIdNumber(CommonFun.getRelVid(consumerUserBankCard.getIdNumber()));
		ConsumerUserBankCard currentConsumerUserBankCard = consumerUserBankCardService.selectByPrimaryKey(id);

		if (currentConsumerUserBankCard == null) {
			return new ResponseRestEntity<ConsumerUserBankCard>(HttpRestStatus.NOT_FOUND);
		}
		consumerUserBankCard.setBankBindId(id);
		currentConsumerUserBankCard.setUserId(consumerUserBankCard.getUserId());
		currentConsumerUserBankCard.setIdNumber(consumerUserBankCard.getIdNumber());
		currentConsumerUserBankCard.setHolerName(consumerUserBankCard.getHolerName());
		currentConsumerUserBankCard.setBankId(consumerUserBankCard.getBankId());
		currentConsumerUserBankCard.setCardNo(consumerUserBankCard.getCardNo());
		currentConsumerUserBankCard.setType(consumerUserBankCard.getType());
		currentConsumerUserBankCard.setMobileNo(consumerUserBankCard.getMobileNo());
		currentConsumerUserBankCard.setRemark(consumerUserBankCard.getRemark());
		consumerUserBankCardService.updateByPrimaryKeySelective(currentConsumerUserBankCard);// ?
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentConsumerUserBankCard,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUserBankCard>(currentConsumerUserBankCard, HttpRestStatus.OK);
	}

	// 删除指定Clap卡
	@PreAuthorize("hasRole('R_BUYER_B_C_E')")
	@ApiOperation(value = "Delete ConsumerUserBankCard", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerUserBankCard> delete(@PathVariable("id") String id) {

		ConsumerUserBankCard consumerUserBankCard = consumerUserBankCardService.selectByPrimaryKey(id);
		if (consumerUserBankCard == null) {
			return new ResponseRestEntity<ConsumerUserBankCard>(HttpRestStatus.NOT_FOUND);
		}

		consumerUserBankCard.setDeleteFlag(1);
		consumerUserBankCardService.updateByPrimaryKeySelective(consumerUserBankCard);
		//删除日志开始
				ConsumerUserBankCard consumer = new ConsumerUserBankCard();
				consumer.setBankBindId(id);
		    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, consumer,CommonLogImpl.CONSUMER);
			//删除日志结束
		return new ResponseRestEntity<ConsumerUserBankCard>(HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_BUYER_B_C_Q')")
	@ApiOperation(value = "Query ConsumerUserBankCard by userId", notes = "")
	@RequestMapping(value = "/userid/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<List<ConsumerUserBankCard>> selectByUserId(@PathVariable("userId") String userId) {
		List<ConsumerUserBankCard> consumerUserBankCardList = consumerUserBankCardService.selectByUserId(userId);
		if (consumerUserBankCardList == null) {
			consumerUserBankCardList = new ArrayList<>();
		}
		return new ResponseRestEntity<List<ConsumerUserBankCard>>(consumerUserBankCardList, HttpRestStatus.OK);
	}

}