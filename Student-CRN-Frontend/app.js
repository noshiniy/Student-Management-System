// Function to set the current date
function setDate() {
  const dateElement = document.getElementById("date");
  const today = new Date();
  const formattedDate = today.toISOString().split("T")[0];
  dateElement.textContent = formattedDate;
}

// Function to generate a new student ID
function generateStudentID() {
  fetch("http://localhost:8080/student/generate-id")
    .then((response) => response.text())
    .then((studentID) => {
      document.getElementById("generatedStudentId").textContent = studentID;
    })
    .catch((error) => console.error("Error fetching student ID:", error));
}

// Function to add a new student
async function addStudent() {
  const student = {
    studentID: document.getElementById("generatedStudentId").textContent,
    title: document.getElementById("title").value,
    firstName: document.getElementById("firstName").value,
    lastName: document.getElementById("lastName").value,
    nic: document.getElementById("nic").value,
    dob: document.getElementById("dob").value,
    gender: document.getElementById("gender").value,
    address: document.getElementById("address").value,
    contactNumber: document.getElementById("contactNumber").value,
    email: document.getElementById("email").value,
    registrationDate: document.getElementById("date").textContent,
  };

  const formData = new FormData();
  formData.append(
    "student",
    new Blob([JSON.stringify(student)], { type: "application/json" })
  );

  // const photoInput = document.getElementById("photo");
  // if (photoInput.files.length > 0) {
  //   formData.append("photo", photoInput.files[0]);
  // }

  try {
    const response = await fetch("http://localhost:8080/student/add", {
      method: "POST",
      body: formData,
    });

    if (!response.ok) {
      throw new Error("Failed to add student");
    }

    alert("Student added successfully!");
    resetForm();
    loadTable();
  } catch (error) {
    console.error("Error adding student:", error);
  }
}

// Function to load students into the table
function loadTable() {
  fetch("http://localhost:8080/student/all")
    .then((response) => response.json())
    .then((students) => {
      const table = document.getElementById("tblStudent");
      table.innerHTML = `
        <thead>
          <tr>
            <th>Student ID</th>
            <th>Title</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>NIC</th>
            <th>Date of Birth</th>
            <th>Gender</th>
            <th>Address</th>
            <th>Contact Number</th>
            <th>Email</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
        </tbody>
      `;

      const tbody = table.querySelector("tbody");
      students.forEach((student) => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td data-label="Student ID">${student.studentID}</td>
          <td data-label="Title">${student.title}</td>
          <td data-label="First Name">${student.firstName}</td>
          <td data-label="Last Name">${student.lastName}</td>
          <td data-label="NIC">${student.nic}</td>
          <td data-label="Date of Birth">${student.dob}</td>
          <td data-label="Gender">${student.gender}</td>
          <td data-label="Address">${student.address}</td>
          <td data-label="Contact Number">${student.contactNumber}</td>
          <td data-label="Email">${student.email}</td>
          <td data-label="Actions">
            <button class="btn btn-warning btn-sm" onclick="editStudent('${student.studentID}')">Update</button>
            <button class="btn btn-danger btn-sm" onclick="deleteStudent('${student.studentID}')">Delete</button>
          </td>
        `;
        tbody.appendChild(row);
      });
    })
    .catch((error) => {
      console.error("Error loading students:", error);
    });
}

// Function to edit a student
function editStudent(id) {
  fetch(`http://localhost:8080/student/find-by-id/${id}`)
    .then((response) => response.json())
    .then((student) => {
      document.getElementById("generatedStudentId").textContent =
        student.studentID;
      document.getElementById("title").value = student.title;
      document.getElementById("firstName").value = student.firstName;
      document.getElementById("lastName").value = student.lastName;
      document.getElementById("gender").value = student.gender;
      document.getElementById("nic").value = student.nic;
      document.getElementById("dob").value = student.dob;
      document.getElementById("address").value = student.address;
      document.getElementById("contactNumber").value = student.contactNumber;
      document.getElementById("email").value = student.email;

      // if (student.photo) {
      //   const photoURL = `data:image/jpeg;base64,${student.photo}`;
      //   document.getElementById("photoPreview").src = photoURL;
      // }

      const button = document.querySelector("button[type='submit']");
      button.textContent = "Update Student";
      button.setAttribute("onclick", `updateStudent('${student.studentID}')`);
    })
    .catch((error) => {
      console.error("Error loading student for editing:", error);
    });
}

// Function to update a student
function updateStudent(id) {
  const formData = new FormData();
  formData.append("studentID", id);
  formData.append("title", document.getElementById("title").value);
  formData.append("firstName", document.getElementById("firstName").value);
  formData.append("lastName", document.getElementById("lastName").value);
  formData.append("gender", document.getElementById("gender").value);
  formData.append("nic", document.getElementById("nic").value);
  formData.append("dob", document.getElementById("dob").value);
  formData.append("address", document.getElementById("address").value);
  formData.append(
    "contactNumber",
    document.getElementById("contactNumber").value
  );
  formData.append("email", document.getElementById("email").value);

  // const photoFile = document.getElementById("photo").files[0];
  // if (photoFile) {
  //   formData.append("photo", photoFile);
  // }

  fetch(`http://localhost:8080/student/update`, {
    method: "PUT",
    body: formData,
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Failed to update student");
      }
      alert("Student updated successfully!");
      resetForm();
      loadTable();
    })
    .catch((error) => {
      console.error("Error updating student:", error);
    });
}

// Function to reset the form
function resetForm() {
  document.getElementById("studentForm").reset();
  generateStudentID();
  const button = document.querySelector("button[type='submit']");
  button.textContent = "Add Student";
  button.setAttribute("onclick", "addStudent()");
}

// Function to delete a student
function deleteStudent(id) {
  if (confirm("Are you sure you want to delete this student?")) {
    fetch(`http://localhost:8080/student/${id}`, {
      method: "DELETE",
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to delete student");
        }
        alert("Student deleted successfully!");
        loadTable();
      })
      .catch((error) => {
        console.error("Error deleting student:", error);
      });
  }
}

// Function to search for students
function searchStudent() {
  const query = document.getElementById("searchInput").value;
  fetch(
    `http://localhost:8080/student/search?query=${encodeURIComponent(query)}`
  )
    .then((response) => response.json())
    .then((students) => {
      const table = document.getElementById("tblStudent");
      table.innerHTML = `
        <thead>
          <tr>
            <th>Student ID</th>
            <th>Title</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>NIC</th>
            <th>Date of Birth</th>
            <th>Gender</th>
            <th>Address</th>
            <th>Contact Number</th>
            <th>Email</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
        </tbody>
      `;

      const tbody = table.querySelector("tbody");
      students.forEach((student) => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${student.studentID}</td>
          <td>${student.title}</td>
          <td>${student.firstName}</td>
          <td>${student.lastName}</td>
          <td>${student.nic}</td>
          <td>${student.dob}</td>
          <td>${student.gender}</td>
          <td>${student.address}</td>
          <td>${student.contactNumber}</td>
          <td>${student.email}</td>
          <td>
            <button class="btn btn-warning btn-sm" onclick="editStudent('${student.studentID}')">Update</button>
            <button class="btn btn-danger btn-sm" onclick="deleteStudent('${student.studentID}')">Delete</button>
          </td>
        `;
        tbody.appendChild(row);
      });
    })
    .catch((error) => {
      console.error("Error searching students:", error);
    });
}

// Initialize date and generate student ID on page load
document.addEventListener("DOMContentLoaded", function () {
  setDate();
  generateStudentID();
  loadTable();
});
