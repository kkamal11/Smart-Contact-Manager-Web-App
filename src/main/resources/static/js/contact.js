const BASE_URL = "http://localhost:8080/";

//////////////////////to populate search text when favourite is selected
const selectFilterField = document.querySelector("#filter-field");
const searchBox = document.querySelector("#table-search-users");

selectFilterField.addEventListener('click', () => {
    selectFilterField.addEventListener('change', () => {
        switch (selectFilterField.value) {
            case "favourite":
                searchBox.setAttribute("value", "All favourite contacts");
                searchBox.disabled = true;
                break;
            case "all":
                searchBox.setAttribute("value", "All contacts");
                searchBox.disabled = true;
                break;
            default:
                searchBox.setAttribute("value", "");
                searchBox.disabled = false;
                break;
        }
    })
})


////////////////////////putting select and search box value based on url
const queryString = window.location.search;
const urlParams = new URLSearchParams(queryString);
let field, fieldVal, keyword, keywordVal;
fieldVal = urlParams.get('field');
keywordVal = urlParams.get('keyword');
if (keywordVal != null) {
    searchBox.setAttribute("value", keywordVal);
}
let selector = document.getElementById("filter-field");
for (let i = 0; i < selector.length; i++) {
    let option = selector.options[i];
    if (option.value == fieldVal) {
        option.selected = true;
        break;
    }
}


///////////////////////Contact details Modal Code
const viewContactModal = document.getElementById("view-contact-modal");
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
    id: 'view-contact-modal',
    override: true
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);
function openContactModal(contactId) {
    //fetch the data from api and set in the dom
    loadContactDataForUser(contactId)
    //show the model with data
    contactModal.show();
}

function closeContactModal() {
    contactModal.hide();
}

function loadContactDataForUser(contactId) {
    const url = `${BASE_URL}api/contacts/${contactId}`;
    fetch(url)
        .then(async (response) => {
            if (response.ok) {
                const data = await response.json();
                setContactModalData(data)
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

function setContactModalData(data) {
    document.getElementById("contact-name").innerHTML = data.name;
    document.getElementById("contact-email").innerHTML = data.email;
}


//delete contact
async function deleteContact(contactId) {
    Swal.fire({
        title: "Are you sure to delete?",
        text: "You won't be able to revert this!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Yes, delete it!"
    }).then((result) => {
        if (result.isConfirmed) {
            const url = `${BASE_URL}user/contacts/delete/${contactId}`;
            window.location.replace(url);
        }
    });
}


const exportData = function () {
    TableToExcel.convert(document.getElementById("contact-table"), {
        name: "contacts.xlsx",
        sheet: {
            name: "Sheet 1"
        }
    });
}
