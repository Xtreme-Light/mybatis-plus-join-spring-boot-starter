package com.github.owl.mybatisplus;

import com.github.owl.mybatisplus.annotations.JoinTable;
import com.github.owl.mybatisplus.annotations.JoinTables;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * <p>
 *  测试连表查询
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/
@JoinTables(joinTables = {
    @JoinTable(tables = {"user_info", "user_role_relation"}, on = {"id", "userId"}),
    @JoinTable(tables = {"user_role_relation", "role_info"}, on = {"roleId", "id"}),
})
@ToString
@EqualsAndHashCode
public class UserInfoAndRoleInfoLogicDeleteEntity {

  private Long t1Id;
  private String t1UserName;

  private Boolean t1Deleted;
  private Long t2UserId;
  private Long t2RoleId;
  private Long t3Id;
  private String t3RoleName;

  private Boolean t3Deleted;

  public Long getT1Id() {
    return t1Id;
  }

  public void setT1Id(Long t1Id) {
    this.t1Id = t1Id;
  }

  public String getT1UserName() {
    return t1UserName;
  }

  public void setT1UserName(String t1UserName) {
    this.t1UserName = t1UserName;
  }

  public Long getT2UserId() {
    return t2UserId;
  }

  public void setT2UserId(Long t2UserId) {
    this.t2UserId = t2UserId;
  }

  public Long getT2RoleId() {
    return t2RoleId;
  }

  public void setT2RoleId(Long t2RoleId) {
    this.t2RoleId = t2RoleId;
  }

  public Long getT3Id() {
    return t3Id;
  }

  public void setT3Id(Long t3Id) {
    this.t3Id = t3Id;
  }

  public String getT3RoleName() {
    return t3RoleName;
  }

  public void setT3RoleName(String t3RoleName) {
    this.t3RoleName = t3RoleName;
  }

  public Boolean getT1Deleted() {
    return t1Deleted;
  }

  public void setT1Deleted(Boolean t1Deleted) {
    this.t1Deleted = t1Deleted;
  }

  public Boolean getT3Deleted() {
    return t3Deleted;
  }

  public void setT3Deleted(Boolean t3Deleted) {
    this.t3Deleted = t3Deleted;
  }
}
