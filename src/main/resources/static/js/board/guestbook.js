// 방명록 등록
function subGuestbook()
{
    const textarea = document.getElementById("guestbookContent");
    if (!textarea) return;

    if (!checkEl(textarea, "내용")) return;
    if (!checkMaxLengthEl(textarea, "내용", 500)) return;

    const content = getValueEl(textarea);

    const secretCheckbox = document.getElementById("guestbookSecret");
    const secret = secretCheckbox ? secretCheckbox.checked : false;

    const payload = { content, secret };

    postJson("/api/guestbook", payload,
    {
        defaultErrorMessage: "방명록 등록 중 오류가 발생했습니다.",
        toastOnSuccess: "방명록이 등록됐습니다.",
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;

        textarea.value = "";

        if (typeof loadGuestbooks === "function") { loadGuestbooks(-1); }
    });
}

// 답변 폼 토글
function toggleForm(btn)
{
    const item = btn.closest(".guestbookItem");
    const form = item?.querySelector("form.guestbookForm[name='answerForm']");
    if (!form) return;

    const willOpen = getComputedStyle(form).display === "none";
    form.style.display = willOpen ? "block" : "none";

    if (willOpen) { form.querySelector("textarea.guestbookContent")?.focus(); }
}

/*
// 방명록 삭제(추후 구현)
function deleteGuestbook() {  }
*/

// 답변 등록
function subAnswer(btn)
{
    const realBtn = btn || window.event?.target;
    if (!realBtn) return;

    const form = realBtn.closest("form.guestbookForm[name='answerForm']");
    if (!form) return;

    const textarea = form.querySelector("textarea.guestbookContent");
    if (!textarea) return;

    if (!checkEl(textarea, "답변 내용")) return;
    if (!checkMaxLengthEl(textarea, "답변 내용", 500)) return;

    const content = getValueEl(textarea);

    // 실제 답변 로직은 추후 구현

    showToast("답변이 등록됐습니다.");
    textarea.value = "";
    form.style.display = "none";
}