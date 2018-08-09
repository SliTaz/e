package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonLogImpl;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.HttpRestStatusFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.MerchantRoleService;
import com.zbensoft.e.payment.db.domain.MerchantRole;
import com.zbensoft.e.payment.db.domain.MerchantUser;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/merchantRole")
@RestController
public class MerchantRoleController {
	@Autowired
	MerchantRoleService merchantRoleService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@ApiOperation(value = "Query merchantRole,Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<MerchantRole>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String name, @RequestParam(required = false) String roleId,
			@RequestParam(required = false) String remark, @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		MerchantRole role = new MerchantRole();
		role.setRoleId(id);
		role.setName(name);
		role.setRemark(remark);


		int count = merchantRoleService.count(role);
		if (count == 0) {
			return new ResponseRestEntity<List<MerchantRole>>(new ArrayList<MerchantRole>(), HttpRestStatus.NOT_FOUND);
		}

		List<MerchantRole> list = null;// consumerUserService.selectPage(consumerUser);

		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			// 第一个参数是第几页；第二个参数是每页显示条数。
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = merchantRoleService.selectPage(role);
		} else {
			list = merchantRoleService.selectPage(role);
		}

		return new ResponseRestEntity<List<MerchantRole>>(list, HttpRestStatus.OK, count, count);
	}
	@ApiOperation(value = "Add merchantRole", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createRole(@RequestBody MerchantRole role,BindingResult result, UriComponentsBuilder ucBuilder) {
		role.setRoleId(IDGenerate.generateCommOne(IDGenerate.MERCHANT_ROLE));
		if (merchantRoleService.isRoleExist(role)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}
		if (result.hasErrors()) {
			List<ObjectError> list = result.getAllErrors();
			return new ResponseRestEntity<Void>(HttpRestStatusFactory.createStatus(list),
					HttpRestStatusFactory.createStatusMessage(list));
		}

		merchantRoleService.insert(role);
		//新增日志
		CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_INSERT, role,CommonLogImpl.MERCHANT);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/role/{id}").buildAndExpand(role.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@ApiOperation(value = "Edit merchantRole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<MerchantRole> updateRole(@PathVariable("id") String id, @RequestBody MerchantRole role) {

		MerchantRole currentRole = merchantRoleService.selectByPrimaryKey(id);

		if (currentRole == null) {
			return new ResponseRestEntity<MerchantRole>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentRole.setRoleId(role.getRoleId());
		currentRole.setCode(role.getCode());
		currentRole.setName(role.getName());
		currentRole.setRemark(role.getRemark());
		merchantRoleService.updateByPrimaryKey(currentRole);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, currentRole,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantRole>(currentRole, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@ApiOperation(value = "Edit Part merchantRole", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<MerchantRole> updateRoleSelective(@PathVariable("id") String id, @RequestBody MerchantRole role) {

		MerchantRole currentRole = merchantRoleService.selectByPrimaryKey(id);

		if (currentRole == null) {
			return new ResponseRestEntity<MerchantRole>(HttpRestStatus.NOT_FOUND);
		}
		merchantRoleService.updateByPrimaryKeySelective(role);
		//修改日志
				CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_UPDATE, role,CommonLogImpl.MERCHANT);
		return new ResponseRestEntity<MerchantRole>(currentRole, HttpRestStatus.OK);
	}

	@ApiOperation(value = "Delete merchantRole", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<MerchantRole> deleteRole(@PathVariable("id") String id) {

		MerchantRole role = merchantRoleService.selectByPrimaryKey(id);
		if (role == null) {
			return new ResponseRestEntity<MerchantRole>(HttpRestStatus.NOT_FOUND);
		}

		merchantRoleService.deleteByPrimaryKey(id);
		//删除日志开始
		MerchantRole merchant = new MerchantRole();
		merchant.setRoleId(id);
    	CommonLogImpl.insertLog(CommonLogImpl.OPERTYPE_DELETE, merchant,CommonLogImpl.MERCHANT);
	//删除日志结束
		return new ResponseRestEntity<MerchantRole>(HttpRestStatus.NO_CONTENT);
	}

}
