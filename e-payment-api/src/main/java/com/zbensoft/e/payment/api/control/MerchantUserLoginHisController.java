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
import com.zbensoft.e.payment.api.service.api.MerchantUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserLoginHis;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantUserLoginHis")
@RestController
public class MerchantUserLoginHisController {
	@Autowired
	MerchantUserLoginHisService merchantUserLoginHisService;
	@Autowired
	MerchantUserService merchantUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	// 查询用户clap卡，支持分页
	@PreAuthorize("hasRole('R_SELLER_L_L_Q')")
	@ApiOperation(value = "Query MerchantUserLoginHis, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantUserLoginHis>> selectPage(@RequestParam(required = false) String consumerUserLoginHisId,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String loginTimeStart,@RequestParam(required = false) String loginTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) Integer loginType,
			@RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		MerchantUserLoginHis merchantUserLoginHis = new MerchantUserLoginHis();
		// 必须输入一个进行查询
		if ((consumerUserLoginHisId == null || "".equals(consumerUserLoginHisId)) && (userId == null || "".equals(userId)) && (idNumber == null || "".equals(idNumber))
				&& (loginTimeStart == null || "".equals(loginTimeStart))
				&& (loginTimeEnd == null || "".equals(loginTimeEnd))&& (loginType == null || "".equals(loginType))) {
			return new ResponseRestEntity<List<MerchantUserLoginHis>>(new ArrayList<MerchantUserLoginHis>(), HttpRestStatus.NOT_FOUND);
		}
		
		if (idNumber == null || "".equals(idNumber)) {
			merchantUserLoginHis.setUserId(userId);
		} else {
			MerchantUser merchantUser = merchantUserService.selectByIdNumber(idNumber);
			if (merchantUser == null) {
				return new ResponseRestEntity<List<MerchantUserLoginHis>>(new ArrayList<MerchantUserLoginHis>(), HttpRestStatus.NOT_FOUND);
			} else {
				if (userId == null || "".equals(userId)) {
					merchantUserLoginHis.setUserId(merchantUser.getUserId());
				} else {
					if (userId.equals(merchantUser.getUserId())) {
						merchantUserLoginHis.setUserId(userId);
					} else {
						return new ResponseRestEntity<List<MerchantUserLoginHis>>(new ArrayList<MerchantUserLoginHis>(), HttpRestStatus.NOT_FOUND);
					}
				}
			}
		}
		merchantUserLoginHis.setConsumerUserLoginHisId(consumerUserLoginHisId);
		merchantUserLoginHis.setLoginType(loginType);
 		merchantUserLoginHis.setLoginTimeStart(loginTimeStart);
 		merchantUserLoginHis.setLoginTimeEnd(loginTimeEnd);
		List<MerchantUserLoginHis> list = null;//merchantUserLoginHisService.selectPage(merchantUserLoginHis);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantUserLoginHisService.selectPage(merchantUserLoginHis);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantUserLoginHisService.selectPage(merchantUserLoginHis);
		}

		int count = merchantUserLoginHisService.count(merchantUserLoginHis);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantUserLoginHis>>(new ArrayList<MerchantUserLoginHis>(), HttpRestStatus.NOT_FOUND);
		}
		List<MerchantUserLoginHis> listNew = new ArrayList<MerchantUserLoginHis>();
		for(MerchantUserLoginHis bean:list){
			MerchantUser merchantUsers = merchantUserService.selectByPrimaryKey(bean.getUserId());
			if(merchantUsers!=null){
				bean.setUserName(merchantUsers.getUserName());
				bean.setIdNumber(merchantUsers.getIdNumber());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<MerchantUserLoginHis>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询用户
	@PreAuthorize("hasRole('R_SELLER_L_L_Q')")
	@ApiOperation(value = "Query MerchantUserLoginHis", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantUserLoginHis> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantUserLoginHis merchantUserLoginHis = merchantUserLoginHisService.selectByPrimaryKey(id);
		if (merchantUserLoginHis == null) {
			return new ResponseRestEntity<MerchantUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantUserLoginHis>(merchantUserLoginHis, HttpRestStatus.OK);
	}

	// 新增用户
	@PreAuthorize("hasRole('R_SELLER_L_L_E')")
	@ApiOperation(value = "Add MerchantUserLoginHis", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createUserClap(@Valid @RequestBody MerchantUserLoginHis merchantUserLoginHis, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		//TODO 自动生成的ID需要引入共通方法
		merchantUserLoginHis.setConsumerUserLoginHisId(IDGenerate.generateCommOne(IDGenerate.MERCHANT_USER_LOGIN_HIS));
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantUserLoginHisService.insert(merchantUserLoginHis);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantUserLoginHis,CommonLogImpl.MERCHANT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/userClap/{id}").buildAndExpand(merchantUserLoginHis.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户clap卡信息
	@PreAuthorize("hasRole('R_SELLER_L_L_E')")
	@ApiOperation(value = "Edit MerchantUserLoginHis", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantUserLoginHis> updateUserClap(@PathVariable("id") String id,
			@Valid @RequestBody MerchantUserLoginHis merchantUserLoginHis, BindingResult result) {

		MerchantUserLoginHis merchantUserLoginHisRst = merchantUserLoginHisService.selectByPrimaryKey(id);

		if (merchantUserLoginHisRst == null) {
			return new ResponseRestEntity<MerchantUserLoginHis>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
//		currentUserClap.setClapNo(clapUseStore.getClapNo());
		merchantUserLoginHisRst.setUserId(merchantUserLoginHis.getUserId());
		merchantUserLoginHisRst.setIpAddr(merchantUserLoginHis.getIpAddr());
		merchantUserLoginHisRst.setLoginTime(merchantUserLoginHis.getLoginTime());
		merchantUserLoginHisRst.setLoginType(merchantUserLoginHis.getLoginType());
		merchantUserLoginHisRst.setAppVersion(merchantUserLoginHis.getAppVersion());
		merchantUserLoginHisRst.setMobileInfo(merchantUserLoginHis.getMobileInfo());
		merchantUserLoginHisRst.setRemark(merchantUserLoginHis.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantUserLoginHis>(merchantUserLoginHisRst, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		merchantUserLoginHisService.updateByPrimaryKey(merchantUserLoginHisRst);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantUserLoginHisRst,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUserLoginHis>(merchantUserLoginHisRst, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户clap卡信息
	@PreAuthorize("hasRole('R_SELLER_L_L_E')")
	@ApiOperation(value = "Edit part MerchantUserLoginHis", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantUserLoginHis> updateUserClapSelective(@PathVariable("id") String id,
			@RequestBody MerchantUserLoginHis merchantUserLoginHis) {

		MerchantUserLoginHis currentMerchantUserLoginHis = merchantUserLoginHisService.selectByPrimaryKey(id);

		if (currentMerchantUserLoginHis == null) {
			return new ResponseRestEntity<MerchantUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		merchantUserLoginHis.setUserId(id);
		merchantUserLoginHisService.updateByPrimaryKeySelective(merchantUserLoginHis);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantUserLoginHis,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantUserLoginHis>(currentMerchantUserLoginHis, HttpRestStatus.OK);
	}

	// 删除指定用户clap卡信息
	@PreAuthorize("hasRole('R_SELLER_L_L_E')")
	@ApiOperation(value = "Delete MerchantUserLoginHis", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantUserLoginHis> deleteUserClap(@PathVariable("id") String id) {

		MerchantUserLoginHis merchantUserLoginHis = merchantUserLoginHisService.selectByPrimaryKey(id);
		if (merchantUserLoginHis == null) {
			return new ResponseRestEntity<MerchantUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}

		merchantUserLoginHisService.deleteByPrimaryKey(id);
		//删除日志开始
		MerchantUserLoginHis merchant = new MerchantUserLoginHis();
		merchant.setConsumerUserLoginHisId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
		return new ResponseRestEntity<MerchantUserLoginHis>(HttpRestStatus.NO_CONTENT);
	}


	//批量
	@PreAuthorize("hasRole('R_SELLER_L_L_E')")
	@ApiOperation(value = "Delete Many SysLogs", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantUserLoginHis> deleteSysLogMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        		merchantUserLoginHisService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<MerchantUserLoginHis>(HttpRestStatus.NO_CONTENT);
	}
}