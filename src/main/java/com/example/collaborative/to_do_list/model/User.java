package com.example.collaborative.to_do_list.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Column(name = "name", length = 50)
	private String name;

	@Column(name = "verification_token", length = 100)
	private String verificationToken;

	@Column(name = "is_verified")
	@Builder.Default
	private boolean verified = false;

	@Column(name = "verification_token_expiry")
	private OffsetDateTime verificationTokenExpiry;

	@Column(name = "created_at", updatable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@Column(name = "last_login")
	private OffsetDateTime lastLogin;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	@Column(name = "is_active")
	@Builder.Default
	private boolean active = true;

	@Column(name = "account_locked")
	@Builder.Default
	private boolean accountLocked = false;

	@Column(name = "failed_login_attempts")
	@Builder.Default
	private int failedLoginAttempts = 0;

	@Column(length = 20)
	@Builder.Default
	private String role = "USER";

	@Column(name = "deleted_at")
	private OffsetDateTime deletedAt;

	// Pre-persist hook to set createdAt before saving
	@PrePersist
	protected void onCreate() {
		this.createdAt = OffsetDateTime.now();
		this.updatedAt = OffsetDateTime.now();
	}

	// Pre-update hook to set updatedAt before updating
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = OffsetDateTime.now();
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}

	@Override
	public String toString() {
		return "User(id=" + id + ")"; // Avoid printing 'members'
	}

	// Use only ID for equals/hashCode (JPA best practice)
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User user))
			return false;
		return id != null && id.equals(user.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
