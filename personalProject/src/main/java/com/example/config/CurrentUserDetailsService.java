package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.model.CustomUser;
import com.example.model.Member;
import com.example.model.MemberRole;
import com.example.service.MemberService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

	private static final String ROLE_PREFIX = "ROLE_";
	
	@Autowired
    private MemberService memberService;
//
//    @Autowired
//    public CurrentUserDetailsService(UserService userService, FingerPrintService fingerPrintService, ActionLogService actionLogService) {
//        this.userService = userService;
//        this.fingerPrintService = fingerPrintService;
//        this.actionLogService = actionLogService;
//    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = memberService.findByUemail(email);
//		return new User(member.getUemail(), member.getUpw(), true, true, true, true, makeGrantedAuthority(member.getRoles()));
		CustomUser customUser = new CustomUser(member.getUemail(), member.getUpw(), true, true, true, true, makeGrantedAuthority(member.getRoles()),member.getUname());
		return customUser;
//		return
//				Optional.ofNullable(memberService.findByUemail(email))
//				.filter(m -> m!= null)
//				.map(m -> new SecurityMember(m)).get();
		
//		
//        log.info("Authenticating user with loginId={}", email);
//        User user = memberService.findByUemail(email)
//                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email=%s was not found", email)));
//
//        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        String fingerprint = attr.getRequest().getParameter("fingerprint");
//
//        if (!fingerPrintService.avaliableDeviceAccess(user.getId(), fingerprint)) {
//            //throw new UsernameNotFoundException("접근허용되지 않은 디바이스입니다.");
//        }
//        //action logging
//        actionLogService.log(user.getId(), TagEnum.LOGIN);
//        return new CurrentUserDTO(user);
    }
	
	public static List<GrantedAuthority> makeGrantedAuthority(List<MemberRole> roles){
		List<GrantedAuthority> list = new ArrayList<>();
		roles.forEach(role -> list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getRoleName())));
		return list;
	}
}