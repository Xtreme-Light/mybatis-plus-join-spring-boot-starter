package com.github.owl.mybatisplus;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.owl.mybatisplus.query.JoinQueryWrapper;
import com.github.owl.mybatisplus.query.JoinLambdaQueryWrapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p>
 *
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/
//@MybatisPlusTest
//@ImportAutoConfiguration(MybatisPlusJoinTestApplication.class)
//@AutoConfigureTestDatabase
@SpringBootTest(classes = MybatisPlusJoinTestApplication.class)
public class MybatisPlusJoinTest {

  @Autowired
  private UserInfoAndRoleInfoMapper userInfoAndRoleInfoMapper;
  @Autowired
  private UserInfoAndRoleInfoLogicMapper userInfoAndRoleInfoLogicMapper;
  @Autowired
  private UserInfoAndUserExtLogicMapper userInfoAndUserExtLogicMapper;

  @Test
  void test() {
    final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities = userInfoAndRoleInfoMapper.selectLeftJoinList(
        null);
    userInfoAndRoleInfoEntities.forEach(System.out::println);
    assert userInfoAndRoleInfoEntities.size() == 10;
    final JoinQueryWrapper<UserInfoAndRoleInfoEntity> objectJoinQueryWrapper = new JoinQueryWrapper<>();
    objectJoinQueryWrapper.eq("t1UserName", "路人甲");
    objectJoinQueryWrapper.orderByAsc("t1Id");
    objectJoinQueryWrapper.orderByAsc("t3Id");
    final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities1 = userInfoAndRoleInfoMapper.selectLeftJoinList(
        objectJoinQueryWrapper);
    userInfoAndRoleInfoEntities1.forEach(System.out::println);
    assert userInfoAndRoleInfoEntities1.size() == 3;
    objectJoinQueryWrapper.select("t1UserName");
    final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities2 = userInfoAndRoleInfoMapper.selectLeftJoinList(
        objectJoinQueryWrapper);
    assert userInfoAndRoleInfoEntities2.get(0).getT1UserName().equals("路人甲");
    assert userInfoAndRoleInfoEntities2.get(0).getT3RoleName() == null;
    assert userInfoAndRoleInfoEntities2.get(0).getT2RoleId() == null;
    assert userInfoAndRoleInfoEntities2.get(0).getT1Id() == null;
    objectJoinQueryWrapper.select("t1UserName", "t3RoleName");
    final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities3 = userInfoAndRoleInfoMapper.selectLeftJoinList(
        objectJoinQueryWrapper);
    assert userInfoAndRoleInfoEntities3.get(0).getT1UserName().equals("路人甲");
    assert userInfoAndRoleInfoEntities3.get(0).getT3RoleName().equals("东厂厂主");
    assert userInfoAndRoleInfoEntities3.get(0).getT2RoleId() == null;
    assert userInfoAndRoleInfoEntities3.get(0).getT1Id() == null;
    objectJoinQueryWrapper.selectCount("t1Id");
    final Long aLong = userInfoAndRoleInfoMapper.selectLeftJoinCount(
        objectJoinQueryWrapper);
    assert aLong == 3;
    assert objectJoinQueryWrapper.getSqlSelect().equals("t1.id");


  }

  @Test
  public void lambdaTest() {
    final JoinLambdaQueryWrapper<UserInfoAndRoleInfoEntity> joinLambdaQueryWrapper = new JoinLambdaQueryWrapper<>();
    joinLambdaQueryWrapper.eq(UserInfoAndRoleInfoEntity::getT1UserName, "路人甲");
    joinLambdaQueryWrapper.orderByDesc(UserInfoAndRoleInfoEntity::getT3Id);
    final Long aLong1 = userInfoAndRoleInfoMapper.selectInnerJoinCount(joinLambdaQueryWrapper);
    assert aLong1 == 3;
    joinLambdaQueryWrapper.eq(UserInfoAndRoleInfoEntity::getT3RoleName, "东厂厂主");
    final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities = userInfoAndRoleInfoMapper.selectInnerJoinList(
        joinLambdaQueryWrapper);
    assert userInfoAndRoleInfoEntities.size() == 1;
    joinLambdaQueryWrapper.select(UserInfoAndRoleInfoEntity::getT1UserName,
        UserInfoAndRoleInfoEntity::getT3RoleName);
    final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities1 = userInfoAndRoleInfoMapper.selectInnerJoinList(
        joinLambdaQueryWrapper);
    assert userInfoAndRoleInfoEntities1.get(0).getT1UserName().equals("路人甲");
    assert userInfoAndRoleInfoEntities1.get(0).getT3RoleName().equals("东厂厂主");
    assert userInfoAndRoleInfoEntities1.get(0).getT2UserId()==null;
    final Page<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntityPage = userInfoAndRoleInfoMapper.selectInnerJoinPage(
        Page.of(1, 1,true), joinLambdaQueryWrapper);
    assert userInfoAndRoleInfoEntityPage.getTotal() == 1L;
  }

  /**
   * 如果全局配置了 逻辑删除字段，比如deleted，那么会自动检查t\\d+Deleted字段，如果存在，则将其视为逻辑删除字段。
   * 如果个别表的逻辑删除条件不一样，可以通过在特定字段上增加@TableLogic(delval = "0",value = "1")来指定删除字段对应的值。
   * 可以混用
   */
  @Test
  public void logicDeletedTest() {
    final JoinLambdaQueryWrapper<UserInfoAndRoleInfoLogicDeleteEntity> joinLambdaQueryWrapper = new JoinLambdaQueryWrapper<>();
    joinLambdaQueryWrapper.in(UserInfoAndRoleInfoLogicDeleteEntity::getT1Id, List.of(1L, 2L));
    final Long aLong1 = userInfoAndRoleInfoLogicMapper.selectInnerJoinCount(joinLambdaQueryWrapper);
    assert aLong1 == 5L;
    final JoinLambdaQueryWrapper<UserInfoAndUserExtEntity> joinLambdaQueryWrapper2 = new JoinLambdaQueryWrapper<>();
    joinLambdaQueryWrapper2.eq(UserInfoAndUserExtEntity::getT1UserName, "路人甲");
    joinLambdaQueryWrapper2.eq(UserInfoAndUserExtEntity::getT3Address, "人间");
    final Long aLong = userInfoAndUserExtLogicMapper.selectInnerJoinCount(joinLambdaQueryWrapper2);
    assert aLong == 0L;
  }



}
