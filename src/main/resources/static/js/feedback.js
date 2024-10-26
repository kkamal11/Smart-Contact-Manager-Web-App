///////////////////////Contact details Modal Code
const feedbackModal = document.getElementById("static-modal");
// options with default values
const feedbackModalOptions = {
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
const feedbackModalInstanceOptions = {
    id: 'static-modal',
    override: true
};

const modal = new Modal(feedbackModal, feedbackModalOptions, feedbackModalInstanceOptions);
function openFeedbackModal() {
    modal.show();
}

function closeFeedbackModal() {
    modal.hide();
}