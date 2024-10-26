//Theme change
function saveThemeToLocalStorage(theme) {
    window.localStorage.setItem("theme", theme);
}

function getThemeFromLocalStorage() {
    const savedTheme = window.localStorage.getItem("theme");
    if (savedTheme != null) {
        return savedTheme;
    }
    return "light";
}

function changeTheme() {
    currentTheme = getThemeFromLocalStorage();
    document.querySelector("html").classList.remove(currentTheme);
    if (currentTheme == 'dark') {
        currentTheme = 'light'
    }
    else {
        currentTheme = 'dark';
    }
    saveThemeToLocalStorage(currentTheme);
    document.querySelector("html").classList.add(currentTheme);
    const text = currentTheme == "light" ? "Dark" : "Light";
    const changeThemeBtn = document.querySelector("#theme");
    changeThemeBtn.children[1].textContent = text;
}

//Show date current-date
const showDate = function () {
    const monthNames = ["DUMMY",
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];
    const today = new Date();
    const day = today.getDate();
    const month = monthNames[today.getMonth() + 1]; // Months are zero-indexed, so we add 1
    const year = today.getFullYear();

    // Format the date as DD/MM/YYYY or any desired format
    const formattedDate = `${month} ${day}, ${year}`;

    const spanElement = document.querySelector("#current-date");
    if (spanElement != null) {
        spanElement.innerHTML = formattedDate;
    }
}

const setCheckBoxValue = function () {

}

window.onload = () => {
    currentTheme = getThemeFromLocalStorage();
    document.querySelector("html").classList.add(currentTheme);
    const text = currentTheme == "light" ? "Dark" : "Light";
    const changeThemeBtn = document.querySelector("#theme");
    changeThemeBtn.children[1].textContent = text;
    showDate();
    setCheckBoxValue()
};

const validateForm = function (e) {
    const yesNoBox = document.querySelector("#favContact");
    // console.log(yesNoBox.value.toLowerCase())
    if (yesNoBox.innerHTML.toLowerCase() != "no" || yesNoBox.innerHTML.toLowerCase() == "yes") {
        return true;
    }
    else {
        alert("Please enter Yes or No only");
        e.preventDefault();
    }
}
