package com.authify.service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.authify.entity.UserEntity;
import com.authify.io.ProfileRequest;
import com.authify.io.ProfileResponse;
import com.authify.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	
	@Override
	public ProfileResponse createProfile(ProfileRequest request) {
	    if (userRepository.existsByEmail(request.getEmail())) {
	        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
	    }

	    UserEntity newProfile = convertToUserEntity(request);
	    newProfile = userRepository.save(newProfile);
	    return convertToProfileResponse(newProfile);
	}

	
	private UserEntity convertToUserEntity(ProfileRequest request) {
		return UserEntity.builder()
		.email(request.getEmail())
		.userId(UUID.randomUUID().toString())
		.name(request.getName())
		.password(passwordEncoder.encode(request.getPassword()))
		.isAccountVerified(false)
		.resetOtpExpireAt(0L)
		.verifyOtp(null)
		.verifyOtpExpiredAt(0L)
		.resetOtp(null)
		.build();
	}
	private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
	    return ProfileResponse.builder()
	        .userId(newProfile.getUserId())   // ✅ use UUID string
	        .name(newProfile.getName())
	        .email(newProfile.getEmail())
	        .isAccountVerified(newProfile.getIsAccountVerified())
	        .build();
	}


	@Override
	public ProfileResponse getProfile(String email) {
		// TODO Auto-generated method stub
		UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found: "+email));
		 return convertToProfileResponse(existingUser); 
	}


	@Override
	public void sendResetOtp(String email) {
		// TODO Auto-generated method stub
		UserEntity existingEntity= userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found: "+email));
		
		String otp= String.valueOf(ThreadLocalRandom.current().nextInt(100000,1000000));
		
		long expiryTime= System.currentTimeMillis()+(15*60*1000);
	    existingEntity.setResetOtp(otp);
	    existingEntity.setResetOtpExpireAt(expiryTime);
	    
	    userRepository.save(existingEntity);
	    
	    try {
	    	emailService.sendResetOtpEmail(existingEntity.getEmail(),otp);
	    }
	    catch(Exception ex) {
	    	throw new RuntimeException("Unable to send email");
	    }
	}


	@Override
	public void resetPassword(String email, String otp, String newPassword) {
		// TODO Auto-generated method stub
		
		UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found: "+email));
		if(existingUser.getResetOtp()== null || !existingUser.getResetOtp().equals(otp)) {
			throw new RuntimeException("Invalid OTP");
		}
		if(existingUser.getResetOtpExpireAt()<System.currentTimeMillis()) {
			throw new RuntimeException("OTP Expired");
		}
		existingUser.setPassword(passwordEncoder.encode(newPassword));
		existingUser.setResetOtp(null);
		existingUser.setResetOtpExpireAt(0L);
		userRepository.save(existingUser);
	}


	@Override
	public void sendOtp(String email) {
		// TODO Auto-generated method stub
		  UserEntity existingUser = userRepository.findByEmail(email)
		            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

		  if(existingUser.getIsAccountVerified()) {
			    return;
			}
		  
		  // Generate a 6-digit OTP
		    String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

		    // Expiry time: 15 minutes
		    long expiryTime = System.currentTimeMillis() + (15 * 60 * 1000);

		    existingUser.setVerifyOtp(otp);
		    existingUser.setVerifyOtpExpiredAt(expiryTime);
		    userRepository.save(existingUser);
		
		    try {
		    	emailService.sendOtpEmail(existingUser.getEmail(),otp);
		    	
		    }
		    catch(Exception e) {
		    	throw new RuntimeException("Unable to send email");
		    }
	}


	@Override
	public void verifyOtp(String email, String otp) {
		// TODO Auto-generated method stub
		 UserEntity existingUser = userRepository.findByEmail(email)
		            .orElseThrow(() ->
		                    new UsernameNotFoundException("User not found: " + email));

		    if (existingUser.getVerifyOtp() == null ||
		            !existingUser.getVerifyOtp().equals(otp)) {
		        throw new RuntimeException("Invalid OTP");
		    }

		    if (existingUser.getVerifyOtpExpiredAt() < System.currentTimeMillis()) {
		        throw new RuntimeException("OTP Expired");
		    }

		    existingUser.setIsAccountVerified(true);
		    existingUser.setVerifyOtp(null);
		    existingUser.setVerifyOtpExpiredAt(0L);

		    userRepository.save(existingUser);
	}


	


}
