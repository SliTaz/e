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
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.CaptionAccountService;
import com.zbensoft.e.payment.db.domain.CaptionAccount;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/captionAccount")
@RestController
public class CaptionAccountController {
	@Autowired
	CaptionAccountService captionAccountService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	//查询会计科目，支持分页
	@PreAuthorize("hasRole('R_ACCOUNT_C_Q')")
	@ApiOperation(value = "Query CaptionAccount，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<CaptionAccount>> selectPage(@RequestParam(required = false) String caption_account_code,
			@RequestParam(required = false) String name, @RequestParam(required = false) Integer deleteFlag,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		CaptionAccount captionAccount = new CaptionAccount();
		captionAccount.setCaptionAccountCode(caption_account_code);
		captionAccount.setName(name);
		captionAccount.setDeleteFlag(0);
		
		List<CaptionAccount> list = new ArrayList<CaptionAccount>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = captionAccountService.selectPage(captionAccount);

		} else {
			list = captionAccountService.selectPage(captionAccount);
		}

		int count = captionAccountService.count(captionAccount);
		// 分页 end
		
		
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<CaptionAccount>>(new ArrayList<CaptionAccount>(),HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<CaptionAccount>>(list, HttpRestStatus.OK,count,count);
	}

	//查询会计科目
	@PreAuthorize("hasRole('R_ACCOUNT_C_Q')")
	@ApiOperation(value = "Query CaptionAccount", notes = "")
	@RequestMapping(value = "/{caption_account_code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<CaptionAccount> selectByPrimaryKey(@PathVariable("caption_account_code") String caption_account_code) {
		CaptionAccount captionAccount = captionAccountService.selectByPrimaryKey(caption_account_code);
		if (captionAccount == null) {
			return new ResponseRestEntity<CaptionAccount>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<CaptionAccount>(captionAccount, HttpRestStatus.OK);
	}

	//新增会计科目
	@PreAuthorize("hasRole('R_ACCOUNT_C_E')")
	@ApiOperation(value = "Add CaptionAccount", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> create(@Valid @RequestBody CaptionAccount captionAccount,BindingResult result, UriComponentsBuilder ucBuilder) {

		//captionAccount.setCaptionAccountCode(System.currentTimeMillis()+"");
		//System.out.println("caption_account_code:"+captionAccount.getCaptionAccountCode());
		CaptionAccount bean = captionAccountService.selectByPrimaryKey(captionAccount.getCaptionAccountCode());
		captionAccount.setDeleteFlag(0);
		if (bean !=null) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		captionAccount.setDeleteFlag(PageHelperUtil.DELETE_NO);
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		
		captionAccountService.insert(captionAccount);
		//新增日志
      CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, captionAccount,CommonLogImpl.ACCOUNTING);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/captionAccount/{caption_account_code}").buildAndExpand(captionAccount.getCaptionAccountCode()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	//修改会计科目信息
	@PreAuthorize("hasRole('R_ACCOUNT_C_E')")
	@ApiOperation(value = "Edit CaptionAccount", notes = "")
	@RequestMapping(value = "{caption_account_code}", method = RequestMethod.PUT)
	public ResponseRestEntity<CaptionAccount> update(@PathVariable("caption_account_code") String caption_account_code, @Valid  @RequestBody CaptionAccount captionAccount, BindingResult result) {

		CaptionAccount currentCaptionAccount = captionAccountService.selectByPrimaryKey(caption_account_code);

		if (currentCaptionAccount == null) {
			return new ResponseRestEntity<CaptionAccount>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}

		currentCaptionAccount.setName(captionAccount.getName());
		currentCaptionAccount.setRemark(captionAccount.getRemark());
		
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<CaptionAccount>(currentCaptionAccount,HttpRestStatusFactory.createStatus(list),HttpRestStatusFactory.createStatusMessage(list));
		}
		
		captionAccountService.updateByPrimaryKey(currentCaptionAccount);
		//修改日志
CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentCaptionAccount,CommonLogImpl.ACCOUNTING);
		return new ResponseRestEntity<CaptionAccount>(currentCaptionAccount, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	//修改部分会计科目信息
	@PreAuthorize("hasRole('R_ACCOUNT_C_E')")
	@ApiOperation(value = "Edit Part CaptionAccount", notes = "")
	@RequestMapping(value = "{caption_account_code}", method = RequestMethod.PATCH)
	public ResponseRestEntity<CaptionAccount> updateSelective(@PathVariable("caption_account_code") String caption_account_code, @RequestBody CaptionAccount captionAccount) {

		CaptionAccount currentCaptionAccount = captionAccountService.selectByPrimaryKey(caption_account_code);

		if (currentCaptionAccount == null) {
			return new ResponseRestEntity<CaptionAccount>(HttpRestStatus.NOT_FOUND);
		}
		captionAccount.setCaptionAccountCode(caption_account_code);//?
		captionAccountService.updateByPrimaryKeySelective(captionAccount);
		//修改日志
CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, captionAccount,CommonLogImpl.ACCOUNTING);
		return new ResponseRestEntity<CaptionAccount>(currentCaptionAccount, HttpRestStatus.OK);
	}

	//删除指定会计科目
	@PreAuthorize("hasRole('R_ACCOUNT_C_E')")
	@ApiOperation(value = "Delete CaptionAccount", notes = "")
	@RequestMapping(value = "/{caption_account_code}", method = RequestMethod.DELETE)
	public ResponseRestEntity<CaptionAccount> delete(@PathVariable("caption_account_code") String caption_account_code) {

		CaptionAccount captionAccount = captionAccountService.selectByPrimaryKey(caption_account_code);
		if (captionAccount == null) {
			return new ResponseRestEntity<CaptionAccount>(HttpRestStatus.NOT_FOUND);
		}

		captionAccount.setDeleteFlag(1);
		captionAccountService.updateByPrimaryKeySelective(captionAccount);
		//删除日志开始
		CaptionAccount delBean = new CaptionAccount();
		delBean.setCaptionAccountCode(caption_account_code);
		delBean.setDeleteFlag(1);
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, delBean,CommonLogImpl.ACCOUNTING);
		//删除日志结束
		return new ResponseRestEntity<CaptionAccount>(HttpRestStatus.NO_CONTENT);
	}

}