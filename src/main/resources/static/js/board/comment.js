// 댓글 등록
function subComment()
{
    if (!check("commentInput", "내용")) return;
    if (!checkMaxLength("commentInput", "댓글", 500)) return;

    const value = getValue("commentInput");

    showToast("댓글 등록 완료!");
    document.getElementById("commentInput").value = "";
}


// 대댓글 등록 폼 토글
function toggleRecommentForm(btn)
{
    const comment = btn.closest(".comment");
    const form = comment?.querySelector('form[name="recommentForm"]');
    if (!form) return;

    const isOpen = getComputedStyle(form).display !== "none";

    document.querySelectorAll('form[name="recommentForm"]').forEach(f => f.style.display = "none");

    if (isOpen) { form.style.display = "none"; return; }

    form.style.display = "flex";
    form.querySelector("textarea")?.focus();
}

/* 댓글 삭제(추후 구현)
function deleteComment() {  }
*/

// 대댓글 등록
function subRecomment(btn)
{
    const form = btn.closest('form[name="recommentForm"]');
    const input = form?.querySelector('textarea[name="recommentInput"]');
    if (!input) return;

    if (!checkEl(input, "내용")) return;
    if (!checkMaxLengthEl(input, "댓글", 500)) return;

    const value = getValueEl(input);

    showToast("대댓글 등록 완료!");
    input.value = "";
    form.style.display = "none";
}