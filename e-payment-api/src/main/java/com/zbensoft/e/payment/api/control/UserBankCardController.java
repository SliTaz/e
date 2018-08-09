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
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.SysUserBankCardService;
import com.zbensoft.e.payment.db.domain.SysUserBankCard;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/userBankCard")
@RestController
public class UserBankCardController {
	@Autowired
	SysUserBankCardService sysUserBankCardService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	// 查询用户银行卡，支持分页
	@ApiOperation(value = "Check the user bank card to support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysUserBankCard>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String bankId,
			@RequestParam(required = false) String cardNo, @RequestParam(required = false) Integer type,
			@RequestParam(required = false) String mobileNo, @RequestParam(required = false) String remark,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		SysUserBankCard userBankCard = new SysUserBankCard();
		userBankCard.setBankBindId(id);
		userBankCard.setUserId(userId);
		userBankCard.setBankId(bankId);
		userBankCard.setCardNo(cardNo);
		userBankCard.setType(type);
		userBankCard.setMobileNo(mobileNo);
		userBankCard.setRemark(remark);

		List<SysUserBankCard> list = sysUserBankCardService.selectPage(userBankCard);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = sysUserBankCardService.selectPage(userBankCard);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = sysUserBankCardService.selectPage(userBankCard);
		}

		int count = sysUserBankCardService.count(userBankCard);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SysUserBankCard>>(new ArrayList<SysUserBankCard>(),
					HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<SysUserBankCard>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户银行卡
	@ApiOperation(value = "Query the user bank card", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysUserBankCard> selectByPrimaryKey(@PathVariable("id") String id) {
		SysUserBankCard sysUserBankCard = sysUserBankCardService.selectByPrimaryKey(id);
		if (sysUserBankCard == null) {
			return new ResponseRestEntity<SysUserBankCard>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysUserBankCard>(sysUserBankCard, HttpRestStatus.OK);
	}

	// 新增用户银行卡
	@ApiOperation(value = "Add a user bank card", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createUserBankCard(@Valid @RequestBody SysUserBankCard userBankCard,
			BindingResult result, UriComponentsBuilder ucBuilder) {

		userBankCard.setBankBindId(System.currentTimeMillis() + "");
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		sysUserBankCardService.insert(userBankCard);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/userClap/{id}").buildAndExpand(userBankCard.getBankBindId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.userbank.success"));
	}

	// 修改用户银行卡信息
	@ApiOperation(value = "Modify the user's bank card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUserBankCard> updateUserBankCard(@PathVariable("id") String id,
			@Valid @RequestBody SysUserBankCard userBankCard, BindingResult result) {

		SysUserBankCard currentUserBankCard = sysUserBankCardService.selectByPrimaryKey(id);

		if (currentUserBankCard == null) {
			return new ResponseRestEntity<SysUserBankCard>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("bookkeepking.update.not_found.message"));
		}
		currentUserBankCard.setUserId(userBankCard.getUserId());
		currentUserBankCard.setBankId(userBankCard.getBankId());
		currentUserBankCard.setCardNo(userBankCard.getCardNo());

		currentUserBankCard.setType(userBankCard.getType());
		currentUserBankCard.setMobileNo(userBankCard.getMobileNo());
		currentUserBankCard.setRemark(userBankCard.getRemark());
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<SysUserBankCard>(currentUserBankCard,
					HttpRestStatusFactory.createStatus(list), HttpRestStatusFactory.createStatusMessage(list));
		}

		sysUserBankCardService.updateByPrimaryKey(currentUserBankCard);

		return new ResponseRestEntity<SysUserBankCard>(currentUserBankCard, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.bankcard"));
	}

	// 修改部分用户银行卡信息
	@ApiOperation(value = "Modify some of the user bank card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysUserBankCard> updateUserBankCardSelective(@PathVariable("id") String id,
			@RequestBody SysUserBankCard userBankCard) {

		SysUserBankCard currentUserBankCard = sysUserBankCardService.selectByPrimaryKey(id);

		if (currentUserBankCard == null) {
			return new ResponseRestEntity<SysUserBankCard>(HttpRestStatus.NOT_FOUND);
		}
		userBankCard.setUserId(id);
		sysUserBankCardService.updateByPrimaryKeySelective(userBankCard);

		return new ResponseRestEntity<SysUserBankCard>(currentUserBankCard, HttpRestStatus.OK);
	}

	// 删除指定用户银行卡
	@ApiOperation(value = "Delete the specified user bank card", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysUserBankCard> deleteUserBankCard(@PathVariable("id") String id) {

		SysUserBankCard userBankCard = sysUserBankCardService.selectByPrimaryKey(id);
		if (userBankCard == null) {
			return new ResponseRestEntity<SysUserBankCard>(HttpRestStatus.NOT_FOUND);
		}

		sysUserBankCardService.deleteByPrimaryKey(id);
		return new ResponseRestEntity<SysUserBankCard>(HttpRestStatus.NO_CONTENT);
	}

}