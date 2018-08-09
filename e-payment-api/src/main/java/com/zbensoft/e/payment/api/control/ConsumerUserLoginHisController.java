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
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserLoginHisService;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.ConsumerUserLoginHis;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/consumerUserLoginHis")
@RestController
public class ConsumerUserLoginHisController {
	@Autowired
	ConsumerUserLoginHisService consumerUserLoginHisService;
	@Autowired
	ConsumerUserClapService consumerUserClapService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 查询用户clap卡，支持分页
	@PreAuthorize("hasRole('R_BUYER_L_L_Q')")
	@ApiOperation(value = "Query the clap User Store, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<ConsumerUserLoginHis>> selectPage(@RequestParam(required = false) String consumerUserLoginHisId,
			@RequestParam(required = false) String userId, @RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String loginTimeStart,@RequestParam(required = false) String loginTimeEnd,
			@RequestParam(required = false) Integer loginType,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		idNumber = CommonFun.getRelVid(idNumber);
		ConsumerUserLoginHis consumerUserLoginHis = new ConsumerUserLoginHis();
		// 输入idNumber查询
		if ((idNumber == null || "".equals(idNumber)) && (consumerUserLoginHisId == null || "".equals(consumerUserLoginHisId))&& (userId == null || "".equals(userId))
				&& (loginTimeStart == null || "".equals(loginTimeStart))
				&& (loginTimeEnd == null || "".equals(loginTimeEnd)) && (loginType == null || "".equals(loginType))) {
			return new ResponseRestEntity<List<ConsumerUserLoginHis>>(new ArrayList<ConsumerUserLoginHis>(),
					HttpRestStatus.NOT_FOUND);
		}
	
		

		if (idNumber == null || "".equals(idNumber)) {
			consumerUserLoginHis.setUserId(userId);
		} else {
			ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(idNumber);

 			if (consumerUserClap == null) {
				return new ResponseRestEntity<List<ConsumerUserLoginHis>>(new ArrayList<ConsumerUserLoginHis>(),
						HttpRestStatus.NOT_FOUND);

			} else {
				if (userId == null || "".equals(userId)) {
					consumerUserLoginHis.setUserId(consumerUserClap.getUserId());
				} else {
					if (userId.equals(consumerUserClap.getUserId())) {
						consumerUserLoginHis.setUserId(userId);
					} else {
						return new ResponseRestEntity<List<ConsumerUserLoginHis>>(new ArrayList<ConsumerUserLoginHis>(),
								HttpRestStatus.NOT_FOUND);
					}
				}
			}
		}
		consumerUserLoginHis.setConsumerUserLoginHisId(consumerUserLoginHisId);
		consumerUserLoginHis.setLoginType(loginType);
 		consumerUserLoginHis.setLoginTimeStart(loginTimeStart);
 		consumerUserLoginHis.setLoginTimeEnd(loginTimeEnd);
		
		List<ConsumerUserLoginHis> list = consumerUserLoginHisService.selectPage(consumerUserLoginHis);
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = consumerUserLoginHisService.selectPage(consumerUserLoginHis);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = consumerUserLoginHisService.selectPage(consumerUserLoginHis);
		}

		int count = consumerUserLoginHisService.count(consumerUserLoginHis);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<ConsumerUserLoginHis>>(new ArrayList<ConsumerUserLoginHis>(), HttpRestStatus.NOT_FOUND);
		}
		List<ConsumerUserLoginHis> listNew = new ArrayList<ConsumerUserLoginHis>();
		for(ConsumerUserLoginHis bean:list){
			 ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(bean.getUserId());
			if(consumerUserClap!=null){
				bean.setIdNumber(consumerUserClap.getIdNumber());
			}
			listNew.add(bean);
		}
		return new ResponseRestEntity<List<ConsumerUserLoginHis>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询用户clap卡
	@PreAuthorize("hasRole('R_BUYER_L_L_Q')")
	@ApiOperation(value = "Query the user clap card", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<ConsumerUserLoginHis> selectByPrimaryKey(@PathVariable("id") String id) {
		ConsumerUserLoginHis consumerUserLoginHis = consumerUserLoginHisService.selectByPrimaryKey(id);
		if (consumerUserLoginHis == null) {
			return new ResponseRestEntity<ConsumerUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<ConsumerUserLoginHis>(consumerUserLoginHis, HttpRestStatus.OK);
	}

	// 新增用户clap卡
	@PreAuthorize("hasRole('R_BUYER_L_L_E')")
	@ApiOperation(value = "Add user clap card", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createUserClap(@Valid @RequestBody ConsumerUserLoginHis consumerUserLoginHis, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		//TODO 自动生成的ID需要引入共通方法
		consumerUserLoginHis.setConsumerUserLoginHisId(IDGenerate.generateCommOne(IDGenerate.CONSUMER_LOGIN_HIS));

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		consumerUserLoginHisService.insert(consumerUserLoginHis);
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, consumerUserLoginHis,CommonLogImpl.CONSUMER);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/userClap/{id}").buildAndExpand(consumerUserLoginHis.getUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户clap卡信息
	@PreAuthorize("hasRole('R_BUYER_L_L_E')")
	@ApiOperation(value = "Modify the user clap card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<ConsumerUserLoginHis> updateUserClap(@PathVariable("id") String id,
			@Valid @RequestBody ConsumerUserLoginHis consumerUserLoginHis, BindingResult result) {

		ConsumerUserLoginHis consumerUserLoginHisRst = consumerUserLoginHisService.selectByPrimaryKey(id);

		if (consumerUserLoginHisRst == null) {
			return new ResponseRestEntity<ConsumerUserLoginHis>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
//		currentUserClap.setClapNo(clapUseStore.getClapNo());
		consumerUserLoginHisRst.setUserId(consumerUserLoginHis.getUserId());
		consumerUserLoginHisRst.setIpAddr(consumerUserLoginHis.getIpAddr());
		consumerUserLoginHisRst.setAppVersion(consumerUserLoginHis.getAppVersion());
		consumerUserLoginHisRst.setLoginType(consumerUserLoginHis.getLoginType());
		consumerUserLoginHisRst.setMobileInfo(consumerUserLoginHis.getMobileInfo());
		consumerUserLoginHisRst.setLoginTime(consumerUserLoginHis.getLoginTime());
		consumerUserLoginHisRst.setRemark(consumerUserLoginHis.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<ConsumerUserLoginHis>(consumerUserLoginHisRst, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		consumerUserLoginHisService.updateByPrimaryKey(consumerUserLoginHisRst);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, consumerUserLoginHisRst,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUserLoginHis>(consumerUserLoginHisRst, HttpRestStatus.OK, localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户clap卡信息
	@PreAuthorize("hasRole('R_BUYER_L_L_E')")
	@ApiOperation(value = "Modify some of the user clap card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<ConsumerUserLoginHis> updateUserClapSelective(@PathVariable("id") String id,
			@RequestBody ConsumerUserLoginHis userClap) {

		ConsumerUserLoginHis currentUserClap = consumerUserLoginHisService.selectByPrimaryKey(id);

		if (currentUserClap == null) {
			return new ResponseRestEntity<ConsumerUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		userClap.setUserId(id);
		consumerUserLoginHisService.updateByPrimaryKeySelective(userClap);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, userClap,CommonLogImpl.CONSUMER);
		return new ResponseRestEntity<ConsumerUserLoginHis>(currentUserClap, HttpRestStatus.OK);
	}

	// 删除指定用户clap卡信息
	@PreAuthorize("hasRole('R_BUYER_L_L_E')")
	@ApiOperation(value = "Delete the specified user clap card information", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerUserLoginHis> deleteUserClap(@PathVariable("id") String id) {

		ConsumerUserLoginHis userClap = consumerUserLoginHisService.selectByPrimaryKey(id);
		if (userClap == null) {
			return new ResponseRestEntity<ConsumerUserLoginHis>(HttpRestStatus.NOT_FOUND);
		}

		consumerUserLoginHisService.deleteByPrimaryKey(id);
		//删除日志开始
				ConsumerUserLoginHis consumer = new ConsumerUserLoginHis();
				consumer.setConsumerUserLoginHisId(id);
		    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, consumer,CommonLogImpl.CONSUMER);
			//删除日志结束
		return new ResponseRestEntity<ConsumerUserLoginHis>(HttpRestStatus.NO_CONTENT);
	}


	//批量
	@PreAuthorize("hasRole('R_BUYER_L_L_E')")
	@ApiOperation(value = "Delete Many SysLogs", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<ConsumerUserLoginHis> deleteSysLogMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        		consumerUserLoginHisService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<ConsumerUserLoginHis>(HttpRestStatus.NO_CONTENT);
	}
}