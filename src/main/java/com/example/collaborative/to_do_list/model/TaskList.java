package com.example.collaborative.to_do_list.model;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "personal_lists")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskList {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "uuid", updatable = false, nullable = false)
	private UUID id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = true, length = 7)
	@Builder.Default
	private String color = "#E3E3E3";

	@OneToMany(mappedBy = "list", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<Task> tasks = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@Column(name = "created_at", updatable = false, nullable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	@Column(name = "deleted_at")
	private OffsetDateTime deletedAt;

	@Column(nullable = false)
	private int position;

	@PrePersist
	protected void onCreate() {
		this.createdAt = OffsetDateTime.now();
		this.updatedAt = OffsetDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = OffsetDateTime.now();
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}

	@Override
	public String toString() {
		return "TaskList{" + "id=" + id + ", name='" + name + '\'' + ", color='" + color + '\'' + ", createdBy="
				+ (createdBy != null ? createdBy.getId() : null) + ", createdAt=" + createdAt + ", isDeleted="
				+ isDeleted() + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof TaskList))
			return false;
		TaskList taskList = (TaskList) o;
		return id != null && id.equals(taskList.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
