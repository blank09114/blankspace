/* 카테고리 편집 폼 토글 */
function toggleForms()
{
    const forms = document.querySelector(".forms");
    const isMobile = window.innerWidth < 768;

    if (forms.style.display !== "none" && forms.style.display !== "") { forms.style.display = "none"; return; }
    forms.style.display = isMobile ? "flex" : "grid";
}

/* 카테고리 추가 */
function addCate()
{
    const input = event.target.closest("form").querySelector("input");
    const value = input.value.trim();

    if (!value)
    {
        showToast("카테고리명을 입력하세요.");
        input.focus();
        return;
    }

    showToast(`카테고리 추가: ${value}`);
    input.value = "";
}

/* 카테고리 삭제 */
function deleteCate()
{
    const select = event.target.closest("form").querySelector("select");
    const value = select.value;

    if (!value) { showToast("카테고리를 선택하세요."); return; }

    showToast(`카테고리 삭제: ${value}`);
}

/* 카테고리 편집 */
function editCate()
{
    const form = event.target.closest("form");
    const select = form.querySelector("select");
    const input = form.querySelector("input");

    if (select.selectedIndex === 0) { showToast("카테고리를 선택하세요."); return; }

    const before = select.value;
    const after = input.value.trim();

    if (!after) 
    {
        showToast("변경할 이름을 입력하세요.");
        input.focus();
        return;
    }

    showToast(`카테고리 변경: ${before} → ${after}`);
    input.value = "";
}