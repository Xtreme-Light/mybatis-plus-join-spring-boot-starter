## mybatis-plus 连表查询增强器

和mybatis-plus一样，只做增强，不做改变，这里的改变是针对多表查询的。

### 警告

> 当前仅适配mysql
>
>

### 简介

1. 约定表别名按照表出现的顺序，为t1,t2,...tn
2. 通过Entity上的@JoinTables注解，描述连表Entity的关联关系，一次描述，后续开箱即用

至于产生的联合的Entity和对应的mapper，后续会修改fork mybatis-plus-generate来实现代码的自动生成

### 引入

gradle

```groovy
implementation 'com.github.owl:mybatis-plus-join-spring-boot-starter:1.0-SNAPSHOT'

```

maven

```xml

<dependency>
  <groupId>com.github.owl</groupId>
  <artifactId>mybatis-plus-join-spring-boot-starter</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>

```

### 示例

可以看测试模块，进行了多种测试。本项目并不是想通过代码来实现复杂的多表查询。而只是简单的对常见的多表查询进行快速开发，提高开发效率。复杂SQL和连表查询，推荐用mybatis的原生SQL能力，或者使用其他工具。

功能包括：

1. 两张以上的表的关联查询
2. 自由的左查询、右查询、内查询
3. 支持联合表的逻辑查询，比如A和B联合查询A和B中的未被逻辑删除的数据（与mybatis的逻辑查询想适配，开箱即用）
4. 支持select指定字段

### 代码实例：

可以看测试用例的使用方式。

```java
    final JoinQueryWrapper<UserInfoAndRoleInfoEntity> objectJoinQueryWrapper=new JoinQueryWrapper<>();
    objectJoinQueryWrapper.eq("t1UserName","路人甲");
    objectJoinQueryWrapper.orderByAsc("t1Id");
    objectJoinQueryWrapper.orderByAsc("t3Id");
final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities1=userInfoAndRoleInfoMapper.selectLeftJoinList(
    objectJoinQueryWrapper);

```

等效的SQL

```sql
SELECT t1.id       as t1Id,
       t1.userName as t1UserName,
       t2.userId   as t2UserId,
       t2.roleId   as t2RoleId,
       t3.id       as t3Id,
       t3.roleName as t3RoleName
FROM user_info t1
         LEFT JOIN user_role_relation t2 ON t1.id = t2.userId
         LEFT JOIN role_info t3 ON t2.roleId = t3.id
WHERE (t1.userName = '路人甲')
ORDER BY t1.id ASC, t3.id ASC
```

提供的API 参见com.github.owl.mybatisplus.mapper.JoinMapper和对应的java doc注释

支持Lambda风格的查询 参见com.github.owl.mybatisplus.MybatisPlusJoinTest.lambdaTest

```java
    final JoinLambdaQueryWrapper<UserInfoAndRoleInfoEntity> joinLambdaQueryWrapper=new JoinLambdaQueryWrapper<>();
    joinLambdaQueryWrapper.eq(UserInfoAndRoleInfoEntity::getT1UserName,"路人甲");
    joinLambdaQueryWrapper.orderByDesc(UserInfoAndRoleInfoEntity::getT3Id);
final Long aLong1=userInfoAndRoleInfoMapper.selectInnerJoinCount(joinLambdaQueryWrapper);
    assert aLong1==3;
    joinLambdaQueryWrapper.eq(UserInfoAndRoleInfoEntity::getT3RoleName,"东厂厂主");
final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities=userInfoAndRoleInfoMapper.selectInnerJoinList(
    joinLambdaQueryWrapper);
    assert userInfoAndRoleInfoEntities.size()==1;
    joinLambdaQueryWrapper.select(UserInfoAndRoleInfoEntity::getT1UserName,
    UserInfoAndRoleInfoEntity::getT3RoleName);
final List<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntities1=userInfoAndRoleInfoMapper.selectInnerJoinList(
    joinLambdaQueryWrapper);
    assert userInfoAndRoleInfoEntities1.get(0).getT1UserName().equals("路人甲");
    assert userInfoAndRoleInfoEntities1.get(0).getT3RoleName().equals("东厂厂主");
    assert userInfoAndRoleInfoEntities1.get(0).getT2UserId()==null;
final Page<UserInfoAndRoleInfoEntity> userInfoAndRoleInfoEntityPage=userInfoAndRoleInfoMapper.selectInnerJoinPage(
    Page.of(1,1,true),joinLambdaQueryWrapper);
    assert userInfoAndRoleInfoEntityPage.getTotal()==1L;
```