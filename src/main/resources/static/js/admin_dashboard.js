const BASE_URL = "http://localhost:8080/";

///////////////////////Contact details Modal Code
const viewUserModal = document.getElementById("view-user-modal");
// options with default values
const options = {
    placement: 'bottom-right',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true,
    onHide: () => {
        // console.log('modal is hidden');
    },
    onShow: () => {
        // console.log('modal is shown');
    },
    onToggle: () => {
        // console.log('modal has been toggled');
    },
};

// instance options object
const instanceOptions = {
    id: 'view-user-modal',
    override: true
};

const userModal = new Modal(viewUserModal, options, instanceOptions);
function openUserModal(email) {
    //show the model with data
    loadDataForUser(email)
    userModal.show();
}

function closeUserModal() {
    userModal.hide();
}

function loadDataForUser(email) {
    const url = `${BASE_URL}api/user-data/${email}`;
    fetch(url)
        .then(async (response) => {
            if (response.ok) {
                const data = await response.json();
                setUserModalData(data)
                return data;
            }
            else {
                console.log(response.status);
            }
        })
        .catch((err) => {
            console.log(err)
        })
}

function setUserModalData(data) {
    document.getElementById("user-name").innerHTML = data.name;
    document.getElementById("user-email").innerHTML = data.email;
    document.getElementById("user-profile-img").src = data.profilePicLink;
    document.getElementById("user-about").innerHTML = data.about;
    document.getElementById("user-gender").innerHTML = data.gender;
    if (data.gender == null) {
        document.getElementById("user-gender-not-avlbl").classList.remove("hidden");
    }
    if (data.enabled) {
        document.getElementById("account-status").innerHTML = "Enabled";
    }
    document.getElementById("joined-on").innerHTML = data.joinedOn;
    if (data.phoneNumber == null) {
        document.getElementById("phone-number-not-avlbl").classList.remove("hidden");
        document.getElementById("phone-number").classList.add("hidden");
    }
    else {
        document.getElementById("phone-number").innerHTML = data.phoneNumber;
    }
    document.getElementById("email-verified").innerHTML = data.emailVerified;
    document.getElementById("phone-verified").innerHTML = data.phoneVerified;
    if (data.phoneVerified == true) {
        document.getElementById("phone-icon-verify").classList.remove("hidden");
    }
    document.getElementById("acc-created-using").innerHTML = data.provider;
    const roleList = data.roleList;
    roleList.forEach(role => {
        if (role == 'ROLE_USER') {
            document.getElementById("user-role").innerHTML = 'ROLE_USER';
        }
        if (role == 'ROLE_ADMIN') {
            document.getElementById("admin-role").classList.remove("hidden");
            document.getElementById("admin-role").innerHTML = 'ROLE_ADMIN';
        }
    })
}
