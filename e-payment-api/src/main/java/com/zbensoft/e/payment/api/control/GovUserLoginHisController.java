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
import com.zbensoft.e.payment.api.service.api.GovUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.GovUserService;
import com.zbensoft.e.payment.db.domain.GovUser;
import com.zbensoft.e.payment.db.domain.GovUserLoginHis;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/govUserLoginHis")
@RestController
public class GovUserLoginHisController {
	@Autowired
	GovUserLoginHisService govUserLoginHisService;
	@Autowired
	GovUserService govUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询用户clap卡，支持分页
	@PreAuthorize("hasRole('R_GOV_U_LL_Q')")
	@ApiOperation(value = "Query the clap User Store, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<GovUserLoginHis>> selectPage(@RequestParam(required = false) String consumerUserLoginHisId,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String userName,
			@RequestParam(required = false) String loginTimeStart,@RequestParam(required = false) String loginTimeEnd,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		GovUserLoginHis govUserLoginHis = new GovUserLoginHis();
		govUserLoginHis.setConsumerUserLoginHisId(consumerUserLoginHisId);
		GovUser consumerUserClap = govUserService.selectByUserName(userName);
 		if(consumerUserClap==null){
 			if(userName==null||"".equals(userName)){
 				govUserLoginHis.setUserId(userId);
 			}else {
 				return new ResponseRestEntity<List<GovUserLoginHis>>(new ArrayList<GovUserLoginHis>(), HttpRestStatus.NOT_EXIST);
 			}
		}else{
			if(userId==null||"".equals(userId)){
				govUserLoginHis.setUserId(consumerUserClap.getUserId());
			}else{
				if(consumerUserClap.getUserId().equals(userId)){
					govUserLoginHis.setUserId(userId);
				}else{
					return new ResponseRestEntity<List<GovUserLoginHis>>(new ArrayList<GovUserLoginHis>(), HttpRestStatus.NOT_EXIST);
				}
			}
		}
		
 		govUserLoginHis.setLoginTimeStart(loginTimeStart);
 		govUserLoginHis.setLoginTimeEnd(loginTimeEnd);
		
		List<GovUserLoginHis> list = govUserLoginHisService.selectPage(govUserLoginHis);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = govUserLoginHisService.selectPage(govUserLoginHis);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = govUserLoginHisService.selectPage(govUserLoginHis);
		}

		int count = govUserLoginHisService.count(govUserLoginHis);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<GovUserLoginHis>>(new ArrayList<GovUserLoginHis>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<GovUserLoginHis>>(list, HttpRestStatus.OK, count, count);
	}

	// 查询用户clap卡
	@PreAuthorize("hasRole('R_GOV_U_LL_Q')")
	@ApiOperation(value = "Query the user clap card", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<GovUserLoginHis> selectByPrimaryKey(@PathVariable("id") String id) {
		GovUserLoginHis govUserLoginHis = govUserLoginHisService.selectByPrimaryKey(id);
		if (govUserLoginHis == null) {
			return new ResponseRestEntity<GovUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<GovUserLoginHis>(govUserLoginHis, HttpRestStatus.OK);
	}

	// 新增用户clap卡
	@PreAuthorize("hasRole('R_GOV_U_LL_E')")
	@ApiOperation(value = "Add user clap card", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createUserClap(@Valid @RequestBody GovUserLoginHis govUserLoginHis, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		//TODO 自动生成的ID需要引入共通方法
		govUserLoginHis.setConsumerUserLoginHisId(IDGenerate.generateCommOne(IDGenerate.GOV_USER_LOGIN_HIS));

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		govUserLoginHisService.insert(govUserLoginHis);
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, govUserLoginHis,CommonLogImpl.GOV_USER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/userClap/{id}").buildAndExpand(govUserLoginHis.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户clap卡信息
	@PreAuthorize("hasRole('R_GOV_U_LL_E')")
	@ApiOperation(value = "Modify the user clap card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovUserLoginHis> updateUserClap(@PathVariable("id") String id,
			@Valid @RequestBody GovUserLoginHis govUserLoginHis, BindingResult result) {

		GovUserLoginHis govUserLoginHisRst = govUserLoginHisService.selectByPrimaryKey(id);

		if (govUserLoginHisRst == null) {
			return new ResponseRestEntity<GovUserLoginHis>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
//		currentUserClap.setClapNo(clapUseStore.getClapNo());
		govUserLoginHisRst.setUserId(govUserLoginHis.getUserId());
		govUserLoginHisRst.setIpAddr(govUserLoginHis.getIpAddr());
		govUserLoginHisRst.setLoginTime(govUserLoginHis.getLoginTime());
		govUserLoginHisRst.setRemark(govUserLoginHis.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<GovUserLoginHis>(govUserLoginHisRst, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		govUserLoginHisService.updateByPrimaryKey(govUserLoginHisRst);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, govUserLoginHisRst,CommonLogImpl.GOV_USER);
				
		return new ResponseRestEntity<GovUserLoginHis>(govUserLoginHisRst, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户clap卡信息
	@PreAuthorize("hasRole('R_GOV_U_LL_E')")
	@ApiOperation(value = "Modify some of the user clap card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<GovUserLoginHis> updateUserClapSelective(@PathVariable("id") String id,
			@RequestBody GovUserLoginHis govUserLoginHis) {

		GovUserLoginHis currentGovUserLoginHis = govUserLoginHisService.selectByPrimaryKey(id);

		if (currentGovUserLoginHis == null) {
			return new ResponseRestEntity<GovUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		govUserLoginHis.setUserId(id);
		govUserLoginHisService.updateByPrimaryKeySelective(govUserLoginHis);
		//修改日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, govUserLoginHis,CommonLogImpl.GOV_USER);
		return new ResponseRestEntity<GovUserLoginHis>(currentGovUserLoginHis, HttpRestStatus.OK);
	}

	// 删除指定用户clap卡信息
	@PreAuthorize("hasRole('R_GOV_U_LL_E')")
	@ApiOperation(value = "Delete the specified user clap card information", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GovUserLoginHis> deleteUserClap(@PathVariable("id") String id) {

		GovUserLoginHis userClap = govUserLoginHisService.selectByPrimaryKey(id);
		if (userClap == null) {
			return new ResponseRestEntity<GovUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}

		govUserLoginHisService.deleteByPrimaryKey(id);
		//删除日志开始
		GovUserLoginHis gov = new GovUserLoginHis();
				gov.setConsumerUserLoginHisId(id);
		    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, gov,CommonLogImpl.GOV_USER);
			//删除日志结束
		return new ResponseRestEntity<GovUserLoginHis>(HttpRestStatus.NO_CONTENT);
	}

	//批量
	@PreAuthorize("hasRole('R_GOV_U_LL_E')")
	@ApiOperation(value = "Delete Many SysLogs", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GovUserLoginHis> deleteSysLogMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        		govUserLoginHisService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<GovUserLoginHis>(HttpRestStatus.NO_CONTENT);
	}
}