package SparklePro.sparkle_ws.dbaccess;

public class User {
	private int userId;
	private String username;
	private String password;
	private String email;
	private String role; 
	private String created_ad;
	private String updated_at;


	public User() {
		super();
	}


	public String getUpdated_at() {
		return updated_at;
	}


	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}


	public String getCreated_ad() {
		return created_ad;
	}


	public void setCreated_ad(String created_ad) {
		this.created_ad = created_ad;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}

	
}