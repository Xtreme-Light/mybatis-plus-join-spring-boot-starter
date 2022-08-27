package com.github.owl.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.github.owl.mybatisplus.enums.JoinKeywordEnum;
import java.util.List;
import org.apache.ibatis.annotations.Param;
/*

               :`
                    .:,
                     :::,,.
             ::      `::::::
             ::`    `,:,` .:`
             `:: `::::::::.:`      `:';,`
              ::::,     .:::`   `@++++++++:
               ``        :::`  @+++++++++++#
                         :::, #++++++++++++++`
                 ,:      `::::::;'##++++++++++
                 .@#@;`   ::::::::::::::::::::;
                  #@####@, :::::::::::::::+#;::.
                  @@######+@:::::::::::::.  #@:;
           ,      @@########':::::::::::: .#''':`
           ;##@@@+:##########@::::::::::: @#;.,:.
            #@@@######++++#####'::::::::: .##+,:#`
            @@@@@#####+++++'#####+::::::::` ,`::@#:`
            `@@@@#####++++++'#####+#':::::::::::@.
             @@@@######+++++''#######+##';::::;':,`
              @@@@#####+++++'''#######++++++++++`
               #@@#####++++++''########++++++++'
               `#@######+++++''+########+++++++;
                `@@#####+++++''##########++++++,
                 @@######+++++'##########+++++#`
                @@@@#####+++++############++++;
              ;#@@@@@####++++##############+++,
             @@@@@@@@@@@###@###############++'
           @#@@@@@@@@@@@@###################+:
        `@#@@@@@@@@@@@@@@###################'`
      :@#@@@@@@@@@@@@@@@@@##################,
      ,@@@@@@@@@@@@@@@@@@@@################;
       ,#@@@@@@@@@@@@@@@@@@@##############+`
        .#@@@@@@@@@@@@@@@@@@#############@,
          @@@@@@@@@@@@@@@@@@@###########@,
           :#@@@@@@@@@@@@@@@@##########@,
            `##@@@@@@@@@@@@@@@########+,
              `+@@@@@@@@@@@@@@@#####@:`
                `:@@@@@@@@@@@@@@##@;.
                   `,'@@@@##@@@+;,`
                        ``...``

 _ _     /_ _ _/_. ____  /    _
/ / //_//_//_|/ /_\  /_///_/_\      Talk is cheap. Show me the code.
     _/             /
 */

/**
 * <p>
 * 连表查询mapper
 * </p>
 *
 * @param <T> 数据传输对象
 * @author light
 * @since 2022/8/8
 */
public interface JoinMapper<T> extends Mapper<T> {

  /**
   * 根据 Wrapper 条件，左连接查询全部记录
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @return 总记录数
   */
  List<T> selectLeftJoinList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 Wrapper 条件，左连接查询总记录数
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @return 总记录数
   */
  Long selectLeftJoinCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 entity 条件，左连接查询全部记录（并翻页）
   *
   * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @return 分页数据
   * @param <P> IPage的实现类
   */
  <P extends IPage<T>> P selectLeftJoinPage(P page,
      @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 Wrapper 条件，右连接查询全部记录
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @return 全部记录
   */
  List<T> selectRightJoinList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 Wrapper 条件，右连接查询总记录数
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @return 总记录数
   */
  Long selectRightJoinCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 entity 条件，右连接查询全部记录（并翻页）
   *
   * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @param <P> IPage的实现类
   * @return 分页的数据
   */
  <P extends IPage<T>> P selectRightJoinPage(P page,
      @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 Wrapper 条件，内连接查询全部记录
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @return 全部记录
   */
  List<T> selectInnerJoinList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 Wrapper 条件，内连接查询总记录数
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @return 总记录数
   */
  Long selectInnerJoinCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 entity 条件，内连接查询全部记录（并翻页）
   *
   * @param page         分页查询条件（可以为 RowBounds.DEFAULT）
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @param <P> IPage的实现类
   * @return 分页的数据
   */
  <P extends IPage<T>> P selectInnerJoinPage(P page,
      @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

  /**
   * 根据 queryWrapper 条件，自定义表之间的连接方式，查询全部数据
   * <code>
   * &lt;pre&gt;
   * JoinQueryWrapper&lt;UserInfoAndTempalteInfoRelationE&gt; objectQueryWrapper = new JoinQueryWrapper&lt;UserInfoAndTempalteInfoRelationE&gt;()
   * mapper.selectJoinList(
   *  objectQueryWrapper,
   *  new JoinKeywordEnum[]{JoinKeywordEnum.LEFT_JOIN,JoinKeywordEnum.INNER_JOIN}
   * )
   * &lt;/pre&gt;
   * </code>
   *
   * @param queryWrapper 实体对象封装操作类（可以为 null）
   * @param join  传入的自定义join,多个,用于连接表之间的join三表传入两个，两表传入一个，以此类推
   * @return 查询得到的数据
   */
  List<T> selectJoinList(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper,
      @Param("items") JoinKeywordEnum[] join);

  /**
   * 根据 queryWrapper 条件，自定义表之间的连接方式，查询全部数据的数量
   * <code>
   * &lt;pre&gt;
   * JoinQueryWrapper&lt;UserInfoAndTempalteInfoRelationE&gt; objectQueryWrapper = new JoinQueryWrapper&lt;UserInfoAndTempalteInfoRelationE&gt;()
   * mapper.selectJoinCount(
   *  objectQueryWrapper,
   *  new JoinKeywordEnum[]{JoinKeywordEnum.LEFT_JOIN,JoinKeywordEnum.INNER_JOIN}
   * )
   * &lt;/pre&gt;
   * </code>
   *
   * @param queryWrapper queryWrapper 实体对象封装操作类（可以为 null）
   * @param join 传入的自定义join,多个,用于连接表之间的join三表传入两个，两表传入一个，以此类推
   * @return 查询得到的总数
   */
  Long selectJoinCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper,
      @Param("items") JoinKeywordEnum[] join);


  /**
   * 根据 queryWrapper 条件，自定义表之间的连接方式，查询全部数据的数量
   * <code>
   * &lt;pre&gt;
   * JoinQueryWrapper&lt;UserInfoAndTempalteInfoRelationE&gt; objectQueryWrapper = new JoinQueryWrapper&lt;UserInfoAndTempalteInfoRelationE&gt;()
   * mapper.selectJoinCount(
   *  Page.of(1, 1),
   *  objectQueryWrapper,
   *  new JoinKeywordEnum[]{JoinKeywordEnum.LEFT_JOIN,JoinKeywordEnum.INNER_JOIN}
   * )
   * &lt;/pre&gt;
   * </code>
   * @param page 分页条件
   * @param queryWrapper 查询条件
   * @param join 传入的自定义join,多个,用于连接表之间的join三表传入两个，两表传入一个，以此类推
   * @param <P> 返回数据
   * @return 返回分页数据
   */
  <P extends IPage<T>> P selectJoinPage(P page,
      @Param(Constants.WRAPPER) Wrapper<T> queryWrapper,
      @Param("items") JoinKeywordEnum[] join);
}
