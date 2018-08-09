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
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.SysUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.SysUserService;
import com.zbensoft.e.payment.db.domain.SysUser;
import com.zbensoft.e.payment.db.domain.SysUserLoginHis;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/sysUserLoginHis")
@RestController
public class SysUserLoginHisController {
	@Autowired
	SysUserLoginHisService sysUserLoginHisService;
	@Autowired
	SysUserService sysUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询用户clap卡，支持分页
	@PreAuthorize("hasRole('R_MANAG_U_LL_Q')")
	@ApiOperation(value = "Query SysUserLoginHis, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysUserLoginHis>> selectPage(@RequestParam(required = false) String consumerUserLoginHisId,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String userName,
			@RequestParam(required = false) String loginTimeStart,@RequestParam(required = false) String loginTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		SysUserLoginHis sysUserLoginHis = new SysUserLoginHis();
		sysUserLoginHis.setConsumerUserLoginHisId(consumerUserLoginHisId);
		SysUser consumerUserClap = sysUserService.selectByUserName(userName);
 		if(consumerUserClap==null){
 			if(userName==null||"".equals(userName)){
 				sysUserLoginHis.setUserId(userId);
 			}else {
 				return new ResponseRestEntity<List<SysUserLoginHis>>(new ArrayList<SysUserLoginHis>(), HttpRestStatus.NOT_EXIST);
 			}
		}else{
			if(userId==null||"".equals(userId)){
				sysUserLoginHis.setUserId(consumerUserClap.getUserId());
			}else{
				if(consumerUserClap.getUserId().equals(userId)){
					sysUserLoginHis.setUserId(userId);
				}else{
					return new ResponseRestEntity<List<SysUserLoginHis>>(new ArrayList<SysUserLoginHis>(), HttpRestStatus.NOT_EXIST);
				}
			}
		}
 		
 		sysUserLoginHis.setLoginTimeStart(loginTimeStart);
 		
 		sysUserLoginHis.setLoginTimeEnd(loginTimeEnd);
		
		List<SysUserLoginHis> list = sysUserLoginHisService.selectPage(sysUserLoginHis);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = sysUserLoginHisService.selectPage(sysUserLoginHis);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = sysUserLoginHisService.selectPage(sysUserLoginHis);
		}

		int count = sysUserLoginHisService.count(sysUserLoginHis);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SysUserLoginHis>>(new ArrayList<SysUserLoginHis>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<SysUserLoginHis>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户clap卡
	@PreAuthorize("hasRole('R_MANAG_U_LL_Q')")
	@ApiOperation(value = "Query SysUserLoginHis", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysUserLoginHis> selectByPrimaryKey(@PathVariable("id") String id) {
		SysUserLoginHis sysUserClap = sysUserLoginHisService.selectByPrimaryKey(id);
		if (sysUserClap == null) {
			return new ResponseRestEntity<SysUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysUserLoginHis>(sysUserClap, HttpRestStatus.OK);
	}

	// 新增用户clap卡
	@PreAuthorize("hasRole('R_MANAG_U_LL_E')")
	@ApiOperation(value = "Add SysUserLoginHis", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createUserClap(@Valid @RequestBody SysUserLoginHis clapUseStore, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		//TODO 自动生成的ID需要引入共通方法
		
		clapUseStore.setConsumerUserLoginHisId(IDGenerate.generateCommOne(IDGenerate.SYS_LOGIN_HIS));

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		sysUserLoginHisService.insert(clapUseStore);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, clapUseStore,CommonLogImpl.MANAGE_USER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/userClap/{id}").buildAndExpand(clapUseStore.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户clap卡信息
	@PreAuthorize("hasRole('R_MANAG_U_LL_E')")
	@ApiOperation(value = "Edit SysUserLoginHis", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysUserLoginHis> updateUserClap(@PathVariable("id") String id,
			@Valid @RequestBody SysUserLoginHis sysUserLoginHis, BindingResult result) {

		SysUserLoginHis sysUserLoginHisRst = sysUserLoginHisService.selectByPrimaryKey(id);

		if (sysUserLoginHisRst == null) {
			return new ResponseRestEntity<SysUserLoginHis>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
//		currentUserClap.setClapNo(clapUseStore.getClapNo());
		sysUserLoginHisRst.setUserId(sysUserLoginHis.getUserId());
		sysUserLoginHisRst.setIpAddr(sysUserLoginHis.getIpAddr());
		sysUserLoginHisRst.setLoginTime(sysUserLoginHis.getLoginTime());
		sysUserLoginHisRst.setRemark(sysUserLoginHis.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<SysUserLoginHis>(sysUserLoginHisRst, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		sysUserLoginHisService.updateByPrimaryKey(sysUserLoginHisRst);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, sysUserLoginHisRst,CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysUserLoginHis>(sysUserLoginHisRst, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户clap卡信息
	@PreAuthorize("hasRole('R_MANAG_U_LL_E')")
	@ApiOperation(value = " Edit part SysUserLoginHis", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysUserLoginHis> updateUserClapSelective(@PathVariable("id") String id,
			@RequestBody SysUserLoginHis userClap) {

		SysUserLoginHis currentUserClap = sysUserLoginHisService.selectByPrimaryKey(id);

		if (currentUserClap == null) {
			return new ResponseRestEntity<SysUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		userClap.setUserId(id);
		sysUserLoginHisService.updateByPrimaryKeySelective(userClap);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, userClap,CommonLogImpl.MANAGE_USER);
		return new ResponseRestEntity<SysUserLoginHis>(currentUserClap, HttpRestStatus.OK);
	}

	// 删除指定用户clap卡信息
	@PreAuthorize("hasRole('R_MANAG_U_LL_E')")
	@ApiOperation(value = "Delete SysUserLoginHis", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysUserLoginHis> deleteUserClap(@PathVariable("id") String id) {

		SysUserLoginHis userClap = sysUserLoginHisService.selectByPrimaryKey(id);
		if (userClap == null) {
			return new ResponseRestEntity<SysUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}

		sysUserLoginHisService.deleteByPrimaryKey(id);
		//删除日志开始
		SysUserLoginHis sys = new SysUserLoginHis();
						sys.setConsumerUserLoginHisId(id);
				    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, sys,CommonLogImpl.MANAGE_USER);
					//删除日志结束
		return new ResponseRestEntity<SysUserLoginHis>(HttpRestStatus.NO_CONTENT);
	}

	//批量
	@PreAuthorize("hasRole('R_MANAG_U_LL_E')")
	@ApiOperation(value = "Delete Many SysLogs", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysUserLoginHis> deleteSysLogMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        		sysUserLoginHisService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<SysUserLoginHis>(HttpRestStatus.NO_CONTENT);
	}
}