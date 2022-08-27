package com.github.owl.mybatisplus;

import com.github.owl.mybatisplus.mapper.JoinMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *
 * </p>
 *
 * @author light
 * @since 2022-08-27
 **/
@Mapper
public interface UserInfoAndRoleInfoMapper extends JoinMapper<UserInfoAndRoleInfoEntity> {

}
