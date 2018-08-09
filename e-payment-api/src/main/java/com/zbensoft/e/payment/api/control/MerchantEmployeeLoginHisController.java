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
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeLoginHisService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeLoginHis;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantEmployeeLoginHis")
@RestController
public class MerchantEmployeeLoginHisController {
	@Autowired
	MerchantEmployeeLoginHisService merchantEmployeeLoginHisService;
	@Autowired
	MerchantEmployeeService merchantEmployeeService;
	@Autowired
	MerchantUserService merchantUserService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;
	// 查询用户clap卡，支持分页
	@PreAuthorize("hasRole('R_SELLER_E_L_L_Q')")
	@ApiOperation(value = "Query the clap Employee Store, support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantEmployeeLoginHis>> selectPage(@RequestParam(required = false) String loginHisId,
			@RequestParam(required = false) String employeeUserId, 	@RequestParam(required = false) String idNumber,
			@RequestParam(required = false) String userName,
			@RequestParam(required = false) String loginTimeStart,@RequestParam(required = false) String loginTimeEnd,
			@RequestParam(required = false) Integer loginType,
			@RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MerchantEmployeeLoginHis merchantEmployeeLoginHis = new MerchantEmployeeLoginHis();
		merchantEmployeeLoginHis.setLoginHisId(loginHisId);
		
		idNumber = CommonFun.getRelVid(idNumber);
		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByIdNumber(CommonFun.getRelVid(userName));//userName=employeeIdNumber
 		if(merchantEmployee==null){
 			if(userName==null||"".equals(userName)){
 				merchantEmployeeLoginHis.setEmployeeUserId(employeeUserId);
 			}else {
 				return new ResponseRestEntity<List<MerchantEmployeeLoginHis>>(new ArrayList<MerchantEmployeeLoginHis>(), HttpRestStatus.NOT_EXIST);
 			}
		}else{
			if(employeeUserId==null||"".equals(employeeUserId)){
				merchantEmployeeLoginHis.setEmployeeUserId(merchantEmployee.getEmployeeUserId());
			}else{
				if(merchantEmployee.getEmployeeUserId().equals(employeeUserId)){
					merchantEmployeeLoginHis.setEmployeeUserId(employeeUserId);
				}else{
					return new ResponseRestEntity<List<MerchantEmployeeLoginHis>>(new ArrayList<MerchantEmployeeLoginHis>(), HttpRestStatus.NOT_EXIST);
				}
			}
		}
		
 		//根据clapStoreNo查询商户对应的所有员工编号，数据库遍历数组查询结果
		if (idNumber != null && !"".equals(idNumber)) {
			MerchantUser merchantUser = merchantUserService.selectByIdNumber(idNumber);
			String[] userIds = null;
			if (merchantUser != null) {
				List<MerchantEmployee> employeeList = merchantEmployeeService.selectByUserId(merchantUser.getUserId());
				if (employeeList != null && employeeList.size() > 0) {
					userIds = new String[employeeList.size()];
					for (int i = 0; i < employeeList.size(); i++) {
						userIds[i] = employeeList.get(i).getEmployeeUserId();
					}
				}
				merchantEmployeeLoginHis.setUserIds(userIds);
			} else {
				return new ResponseRestEntity<List<MerchantEmployeeLoginHis>>(new ArrayList<MerchantEmployeeLoginHis>(), HttpRestStatus.NOT_FOUND);
			}
		}
 		
 		
		merchantEmployeeLoginHis.setLoginTimeStart(loginTimeStart);
		merchantEmployeeLoginHis.setLoginTimeEnd(loginTimeEnd);
		merchantEmployeeLoginHis.setLoginType(loginType);
		
		List<MerchantEmployeeLoginHis> list = null;
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantEmployeeLoginHisService.selectPage(merchantEmployeeLoginHis);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = merchantEmployeeLoginHisService.selectPage(merchantEmployeeLoginHis);
		}

		int count = merchantEmployeeLoginHisService.count(merchantEmployeeLoginHis);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<MerchantEmployeeLoginHis>>(new ArrayList<MerchantEmployeeLoginHis>(), HttpRestStatus.NOT_FOUND);
		}
		
		List<MerchantEmployeeLoginHis> listNew = new ArrayList<MerchantEmployeeLoginHis>();
		for(MerchantEmployeeLoginHis bean:list){
			
			MerchantEmployee merchantEmployeeBean = merchantEmployeeService.selectByPrimaryKey(bean.getEmployeeUserId());
			if(merchantEmployeeBean!=null){
				MerchantUser merchantUserBean =  merchantUserService.selectByPrimaryKey(merchantEmployeeBean.getUserId());
				if(merchantUserBean!=null){
					bean.setIdNumber(merchantUserBean.getIdNumber());
				}
			}
			listNew.add(bean);
		}
		
		return new ResponseRestEntity<List<MerchantEmployeeLoginHis>>(listNew, HttpRestStatus.OK, count, count);
	}

	// 查询用户clap卡
	@PreAuthorize("hasRole('R_SELLER_E_L_L_Q')")
	@ApiOperation(value = "Query the user clap card", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<MerchantEmployeeLoginHis> selectByPrimaryKey(@PathVariable("id") String id) {
		MerchantEmployeeLoginHis merchantEmployeeLoginHis = merchantEmployeeLoginHisService.selectByPrimaryKey(id);
		if (merchantEmployeeLoginHis == null) {
			return new ResponseRestEntity<MerchantEmployeeLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<MerchantEmployeeLoginHis>(merchantEmployeeLoginHis, HttpRestStatus.OK);
	}

	// 新增用户clap卡
	@PreAuthorize("hasRole('R_SELLER_E_L_L_E')")
	@ApiOperation(value = "Add user clap card", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createEmployeeClap(@Valid @RequestBody MerchantEmployeeLoginHis merchantEmployeeLoginHis, BindingResult result,
			UriComponentsBuilder ucBuilder) {
		//TODO 自动生成的ID需要引入共通方法
	
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();

			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}
		merchantEmployeeLoginHisService.insert(merchantEmployeeLoginHis);
		//新增日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, merchantEmployeeLoginHis,CommonLogImpl.MERCHANT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/userClap/{id}").buildAndExpand(merchantEmployeeLoginHis.getEmployeeUserId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED, localeMessageSourceService.getMessage("common.create.created.message"));
	}

	// 修改用户clap卡信息
	@PreAuthorize("hasRole('R_SELLER_E_L_L_E')")
	@ApiOperation(value = "Modify the user clap card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantEmployeeLoginHis> updateEmployeeClap(@PathVariable("id") String id,
			@Valid @RequestBody MerchantEmployeeLoginHis merchantEmployeeLoginHis, BindingResult result) {

		MerchantEmployeeLoginHis merchantEmployeeLoginHisRst = merchantEmployeeLoginHisService.selectByPrimaryKey(id);

		if (merchantEmployeeLoginHisRst == null) {
			return new ResponseRestEntity<MerchantEmployeeLoginHis>(HttpRestStatus.NOT_FOUND, localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
//		currentEmployeeClap.setClapNo(clapUseStore.getClapNo());
		merchantEmployeeLoginHisRst.setEmployeeUserId(merchantEmployeeLoginHis.getEmployeeUserId());
		merchantEmployeeLoginHisRst.setIpAddr(merchantEmployeeLoginHis.getIpAddr());
		merchantEmployeeLoginHisRst.setLoginTime(merchantEmployeeLoginHis.getLoginTime());
		merchantEmployeeLoginHisRst.setRemark(merchantEmployeeLoginHis.getRemark());

		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			for (ObjectError error : list) {
				//System.out.println(error.getCode() + "---" + error.getArguments() + "---" + error.getDefaultMessage());
			}

			return new ResponseRestEntity<MerchantEmployeeLoginHis>(merchantEmployeeLoginHisRst, HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		merchantEmployeeLoginHisService.updateByPrimaryKey(merchantEmployeeLoginHisRst);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantEmployeeLoginHisRst,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployeeLoginHis>(merchantEmployeeLoginHisRst, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	// 修改部分用户clap卡信息
	@PreAuthorize("hasRole('R_SELLER_E_L_L_E')")
	@ApiOperation(value = "Modify some of the user clap card information", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantEmployeeLoginHis> updateEmployeeClapSelective(@PathVariable("id") String id,
			@RequestBody MerchantEmployeeLoginHis merchantEmployeeLoginHis) {

		MerchantEmployeeLoginHis currentMerchantEmployeeLoginHis = merchantEmployeeLoginHisService.selectByPrimaryKey(id);

		if (currentMerchantEmployeeLoginHis == null) {
			return new ResponseRestEntity<MerchantEmployeeLoginHis>(HttpRestStatus.NOT_FOUND);
		}
		merchantEmployeeLoginHis.setEmployeeUserId(id);
		merchantEmployeeLoginHisService.updateByPrimaryKeySelective(merchantEmployeeLoginHis);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, merchantEmployeeLoginHis,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantEmployeeLoginHis>(currentMerchantEmployeeLoginHis, HttpRestStatus.OK);
	}

	// 删除指定用户clap卡信息
	@PreAuthorize("hasRole('R_SELLER_E_L_L_E')")
	@ApiOperation(value = "Delete the specified user clap card information", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantEmployeeLoginHis> deleteEmployeeClap(@PathVariable("id") String id) {

		MerchantEmployeeLoginHis merchantEmployeeLoginHis = merchantEmployeeLoginHisService.selectByPrimaryKey(id);
		if (merchantEmployeeLoginHis == null) {
			return new ResponseRestEntity<MerchantEmployeeLoginHis>(HttpRestStatus.NOT_FOUND);
		}

		merchantEmployeeLoginHisService.deleteByPrimaryKey(id);
		//删除日志开始
		MerchantEmployeeLoginHis merchant = new MerchantEmployeeLoginHis();
		merchant.setEmployeeUserId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
		return new ResponseRestEntity<MerchantEmployeeLoginHis>(HttpRestStatus.NO_CONTENT);
	}
	//批量
	@PreAuthorize("hasRole('R_SELLER_E_L_L_E')")
	@ApiOperation(value = "Delete Many MerchantEmployeeLoginHiss", notes = "")
	@RequestMapping(value = "/deleteMany/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantEmployeeLoginHis> deleteMerchantEmployeeLoginHisMany(@PathVariable("id") String id) {
        String[] idStr = id.split(",");
        if(idStr!=null){
        	for(String str :idStr){
        	  merchantEmployeeLoginHisService.deleteByPrimaryKey(str);
        	}
        }
		return new ResponseRestEntity<MerchantEmployeeLoginHis>(HttpRestStatus.NO_CONTENT);
	}
	

}