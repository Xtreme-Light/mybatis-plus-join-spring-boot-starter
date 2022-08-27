package com.github.owl.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.github.owl.mybatisplus.annotations.JoinTable;
import com.github.owl.mybatisplus.annotations.JoinTables;
import lombok.Data;
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
    @JoinTable(tables = {"user_info", "user_ext_relation"}, on = {"id", "userId"}),
    @JoinTable(tables = {"user_ext_relation", "user_ext"}, on = {"userExtId", "id"}),
})
@ToString
@EqualsAndHashCode
@Data
public class UserInfoAndUserExtEntity {

  private Long t1Id;
  private String t1UserName;
//  @TableLogic
  private Boolean t1Deleted;
  private Long t2UserId;
  private Long t2UserExtId;
  private Long t3Id;
  private String t3Address;

  @TableLogic(delval = "1",value = "0")
  private Integer t3LogicDelete;

}
