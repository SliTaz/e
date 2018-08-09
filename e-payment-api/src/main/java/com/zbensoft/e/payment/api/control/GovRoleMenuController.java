package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.service.api.GovRoleMenuService;
import com.zbensoft.e.payment.db.domain.GovRoleMenuKey;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/govrolemenu")
@RestController
public class GovRoleMenuController {
	@Autowired
	GovRoleMenuService govRoleMenuService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_GOV_U_R_Q')")
	@ApiOperation(value = "Query GovRoleMenu，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<GovRoleMenuKey>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String roleId, @RequestParam(required = false) String menuId,
			 @RequestParam(required = false) String start,
			@RequestParam(required = false) String length) {
		GovRoleMenuKey key = new GovRoleMenuKey();
		key.setRoleId(id);
       key.setMenuId(menuId);
       List<GovRoleMenuKey> list = new ArrayList<GovRoleMenuKey>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = govRoleMenuService.selectPage(key);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = govRoleMenuService.selectPage(key);
		}

		int count = govRoleMenuService.count(key);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<GovRoleMenuKey>>(new ArrayList<GovRoleMenuKey>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<GovRoleMenuKey>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@ApiOperation(value = "add GovRoleMenu", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createRole(@RequestBody GovRoleMenuKey key, UriComponentsBuilder ucBuilder) {
		key.setRoleId(System.currentTimeMillis() + "");
		if (govRoleMenuService.isRoleExist(key)) {
			return new ResponseRestEntity<Void>(HttpRestStatus.CONFLICT,localeMessageSourceService.getMessage("common.create.conflict.message"));
		}

		govRoleMenuService.insert(key);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/role/{id}").buildAndExpand(key.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@ApiOperation(value = "Edit GovRoleMenu", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public ResponseRestEntity<GovRoleMenuKey> updateRole(@PathVariable("id") String id, @RequestBody GovRoleMenuKey key) {

		GovRoleMenuKey currentRole = govRoleMenuService.selectByPrimaryKey(id);

		if (currentRole == null) {
			return new ResponseRestEntity<GovRoleMenuKey>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentRole.setRoleId(key.getRoleId());
		currentRole.setMenuId(key.getMenuId());
		govRoleMenuService.updateByPrimaryKey(currentRole);

		return new ResponseRestEntity<GovRoleMenuKey>(currentRole, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@ApiOperation(value = "Edit part GovRoleMenu", notes = "")
	@RequestMapping(value = "{id}", method = RequestMethod.PATCH)
	public ResponseRestEntity<GovRoleMenuKey> updateRoleSelective(@PathVariable("id") String id, @RequestBody GovRoleMenuKey role) {

		GovRoleMenuKey currentRole = govRoleMenuService.selectByPrimaryKey(id);

		if (currentRole == null) {
			return new ResponseRestEntity<GovRoleMenuKey>(HttpRestStatus.NOT_FOUND);
		}
		govRoleMenuService.updateByPrimaryKeySelective(role);

		return new ResponseRestEntity<GovRoleMenuKey>(currentRole, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_GOV_U_R_E')")
	@ApiOperation(value = "delete GovRoleMenu", notes = "")
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseRestEntity<GovRoleMenuKey> deleteRole(@PathVariable("id") String id) {

		GovRoleMenuKey role = govRoleMenuService.selectByPrimaryKey(id);
		if (role == null) {
			return new ResponseRestEntity<GovRoleMenuKey>(HttpRestStatus.NOT_FOUND);
		}

		govRoleMenuService.deleteByPrimaryKey(role);
		return new ResponseRestEntity<GovRoleMenuKey>(HttpRestStatus.NO_CONTENT);
	}
}
