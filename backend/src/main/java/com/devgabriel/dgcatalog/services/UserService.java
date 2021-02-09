package com.devgabriel.dgcatalog.services;

import com.devgabriel.dgcatalog.dtos.RoleDTO;
import com.devgabriel.dgcatalog.dtos.UserDTO;
import com.devgabriel.dgcatalog.dtos.UserInsertDTO;
import com.devgabriel.dgcatalog.dtos.UserUpdateDTO;
import com.devgabriel.dgcatalog.entities.Role;
import com.devgabriel.dgcatalog.entities.User;
import com.devgabriel.dgcatalog.repositories.RoleRepository;
import com.devgabriel.dgcatalog.repositories.UserRepository;
import com.devgabriel.dgcatalog.services.exceptions.DatabaseException;
import com.devgabriel.dgcatalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
	private static Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository repository;

	@Autowired
	private RoleRepository roleRepository;

	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(PageRequest pageRequest) {
		Page<User> users = repository.findAll(pageRequest);
		return users.map(UserDTO::new);
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User user = obj.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return new UserDTO(user);
	}

	@Transactional
	public UserDTO insert(UserInsertDTO userDTO) {
		User user = new User();
		copyDtoToEntity(userDTO, user);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		user = repository.save(user);
		return new UserDTO(user);
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO userUpdateDTO) {
		try {
			User user = repository.getOne(id);
			copyDtoToEntity(userUpdateDTO, user);
			user = repository.save(user);
			return new UserDTO(user);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id not found " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}

	private void copyDtoToEntity(UserDTO userDTO, User entity) {
		entity.setFirstName(userDTO.getFirstName());
		entity.setLastName(userDTO.getLastName());
		entity.setEmail(userDTO.getEmail());

		entity.getRoles().clear();
		for (RoleDTO roleDto : userDTO.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByEmail(username);
		if (user == null) {
			logger.error("User not found " + username);
			throw new UsernameNotFoundException("Email not found");
		}
		logger.info("User found: " + username);
		return user;
	}
}
