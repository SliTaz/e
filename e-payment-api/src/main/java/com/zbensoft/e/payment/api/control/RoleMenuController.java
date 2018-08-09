package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import com.zbensoft.e.payment.api.service.api.SysRoleMenuService;
import com.zbensoft.e.payment.db.domain.SysRoleMenu;
import com.zbensoft.e.payment.db.domain.SysRoleMenuKey;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/roleMenu")
@RestController
public class RoleMenuController {
	@Autowired
	SysRoleMenuService sysRoleMenuService;
	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	@PreAuthorize("hasRole('R_MANAG_U_R_Q')")
	@ApiOperation(value = "Query SysRoleMenu，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SysRoleMenu>> selectPage(@RequestParam(required = false) String id,
			@RequestParam(required = false) String menuId, @RequestParam(required = false) String userId,
			@RequestParam(required = false) String start, @RequestParam(required = false) String length) {
		SysRoleMenu roleMenu = new SysRoleMenu();
		roleMenu.setRoleId(id);
		roleMenu.setMenuId(menuId);
		List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
		// 分页 start
		if (start != null && length != null) {// 需要进行分页
			/*
			 * 第一个参数是第几页；第二个参数是每页显示条数。
			 */
			int pageNum = PageHelperUtil.getPageNum(start, length);
			int pageSize = PageHelperUtil.getPageSize(start, length);
			PageHelper.startPage(pageNum, pageSize);
			list = sysRoleMenuService.selectPage(roleMenu);
			// System.out.println("pageNum:"+pageNum+";pageSize:"+pageSize);
			// System.out.println("list.size:"+list.size());

		} else {
			list = sysRoleMenuService.selectPage(roleMenu);
		}

		int count = sysRoleMenuService.count(roleMenu);
		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SysRoleMenu>>(new ArrayList<SysRoleMenu>(), HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<List<SysRoleMenu>>(list, HttpRestStatus.OK, count, count);
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_Q')")
	@ApiOperation(value = "Query SysRoleMenu", notes = "")
	@RequestMapping(value = "/{roleId}/{menuId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseRestEntity<SysRoleMenu> selectByPrimaryKey(@PathVariable("roleId") String roleId,
			@PathVariable("menuId") String menuId) {

		SysRoleMenuKey sysRoleMenuKey = new SysRoleMenuKey();
		sysRoleMenuKey.setRoleId(roleId);
		sysRoleMenuKey.setMenuId(menuId);
		SysRoleMenu sysRoleMenu = sysRoleMenuService.selectByPrimaryKey(sysRoleMenuKey);
		if (sysRoleMenu == null) {
			return new ResponseRestEntity<SysRoleMenu>(HttpRestStatus.NOT_FOUND);
		}
		return new ResponseRestEntity<SysRoleMenu>(sysRoleMenu, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@ApiOperation(value = "Add SysRoleMenu", notes = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseRestEntity<Void> createRoleMenu(@RequestBody SysRoleMenu roleMenu, UriComponentsBuilder ucBuilder) {

		roleMenu.setRoleId(System.currentTimeMillis() + "");
		sysRoleMenuService.insert(roleMenu);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/roleMenu/{roleId}").buildAndExpand(roleMenu.getRoleId()).toUri());
		return new ResponseRestEntity<Void>(headers, HttpRestStatus.CREATED,localeMessageSourceService.getMessage("common.create.created.message"));
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@ApiOperation(value = "Edit SysRoleMenu", notes = "")
	@RequestMapping(value = "/{roleId}/{menuId}", method = RequestMethod.PUT)
	public ResponseRestEntity<SysRoleMenu> updateRoleMenu(@PathVariable("roleId") String roleId,
			@PathVariable("menuId") String menuId, @RequestBody SysRoleMenu roleMenu) {

		SysRoleMenuKey sysRoleMenuKey = new SysRoleMenuKey();
		sysRoleMenuKey.setRoleId(roleId);
		sysRoleMenuKey.setMenuId(menuId);
		SysRoleMenu currentRoleMenu = sysRoleMenuService.selectByPrimaryKey(sysRoleMenuKey);

		if (currentRoleMenu == null) {
			return new ResponseRestEntity<SysRoleMenu>(HttpRestStatus.NOT_FOUND,localeMessageSourceService.getMessage("common.update.not_found.message"));
		}
		currentRoleMenu.setRoleId(roleMenu.getRoleId());
		currentRoleMenu.setMenuId(roleMenu.getMenuId());

		sysRoleMenuService.updateByPrimaryKey(currentRoleMenu);

		return new ResponseRestEntity<SysRoleMenu>(currentRoleMenu, HttpRestStatus.OK,localeMessageSourceService.getMessage("common.update.ok.message"));
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@ApiOperation(value = "Edit part SysRoleMenu", notes = "")
	@RequestMapping(value = "/{roleId}/{menuId}", method = RequestMethod.PATCH)
	public ResponseRestEntity<SysRoleMenu> updateRoleMenuSelective(@PathVariable("roleId") String roleId,
			@PathVariable("menuId") String menuId, @RequestBody SysRoleMenu roleMenu) {

		SysRoleMenuKey sysRoleMenuKey = new SysRoleMenuKey();
		sysRoleMenuKey.setRoleId(roleId);
		sysRoleMenuKey.setMenuId(menuId);
		SysRoleMenu currentRoleMenu = sysRoleMenuService.selectByPrimaryKey(sysRoleMenuKey);

		if (currentRoleMenu == null) {
			return new ResponseRestEntity<SysRoleMenu>(HttpRestStatus.NOT_FOUND);
		}
		roleMenu.setRoleId(roleId);
		roleMenu.setMenuId(menuId);
		sysRoleMenuService.updateByPrimaryKeySelective(roleMenu);

		return new ResponseRestEntity<SysRoleMenu>(currentRoleMenu, HttpRestStatus.OK);
	}

	@PreAuthorize("hasRole('R_MANAG_U_R_E')")
	@ApiOperation(value = "Delete SysRoleMenu", notes = "")
	@RequestMapping(value = "/{roleId}/{menuId}", method = RequestMethod.DELETE)
	public ResponseRestEntity<SysRoleMenu> deleteRoleMenu(@PathVariable("roleId") String roleId,
			@PathVariable("menuId") String menuId) {
		SysRoleMenuKey sysRoleMenuKey = new SysRoleMenuKey();
		sysRoleMenuKey.setRoleId(roleId);
		sysRoleMenuKey.setMenuId(menuId);
		SysRoleMenu roleMenu = sysRoleMenuService.selectByPrimaryKey(sysRoleMenuKey);
		if (roleMenu == null) {
			return new ResponseRestEntity<SysRoleMenu>(HttpRestStatus.NOT_FOUND);
		}

		sysRoleMenuService.deleteByPrimaryKey(sysRoleMenuKey);
		return new ResponseRestEntity<SysRoleMenu>(HttpRestStatus.NO_CONTENT);
	}
}