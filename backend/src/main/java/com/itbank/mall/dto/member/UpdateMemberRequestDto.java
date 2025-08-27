package com.itbank.mall.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRequestDto {
	private String name;
    private String nickname;
    private String phone;
    private String address1;
    private String address2;
}
