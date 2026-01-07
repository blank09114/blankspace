// 1차/2차 변경 처리
function onTypeChange(selectEl)
{
    const originEl = document.querySelector('[name="origin"]');
    const isSecondary = selectEl.value === "2차";

    if (!originEl) return;

    originEl.readOnly = !isSecondary;
    if (!isSecondary) originEl.value = "";
}

// 소설 등록
function subNovel()
{
    const formEl   = document.getElementById("postForm");
    const typeEl   = document.querySelector('[name="type"]');
    const originEl = document.querySelector('[name="origin"]');
    const nameEl   = document.querySelector('[name="name"]');
    const introEl  = document.querySelector('[name="intro"]');

    const type = typeEl ? typeEl.value : "";

    if (type === "2차") { if (!checkEl(originEl, "원작")) return; }

    if (!checkEl(nameEl, "제목")) return;
    if (!checkEl(introEl, "소개글")) return;

    if (getValueEl(nameEl).length > 100) { showToast("제목은 100자 이내로 작성하세요."); nameEl.focus(); return; }
    if (type === "2차" && getValueEl(originEl).length > 100) { showToast("원작은 100자 이내로 작성하세요."); originEl.focus(); return; }

    HTMLFormElement.prototype.submit.call(formEl);
}

// 회차 등록/수정
function subEpisode()
{
    const formEl = document.getElementById("postForm");

    window.RichEditor?.sync?.();

    const nameEl = document.querySelector('[name="name"]');
    const contentEl = document.querySelector('[name="content"]');

    if (!checkEl(nameEl, "소제목")) return;
    if (!checkEl(contentEl, "회차 내용")) return;

    if (getValueEl(nameEl).length > 20) {
        showToast("소제목은 100자 이내로 작성하세요.");
        nameEl.focus();
        return;
    }

    HTMLFormElement.prototype.submit.call(formEl);
}

// 설정 등록/수정
function subWorld()
{
    const formEl = document.getElementById("postForm");

    const categoryEl = document.querySelector('[name="category"]');
    const nameEl     = document.querySelector('[name="name"]');
    const contentEl  = document.querySelector('[name="content"]');

    window.RichEditor?.sync?.();

    if (!checkEl(categoryEl, "카테고리")) return;
    if (!checkEl(nameEl, "제목")) return;
    if (!checkEl(contentEl, "본문")) return;

    if (getValueEl(nameEl).length > 100) {
        showToast("제목은 100자 이내로 작성하세요.");
        nameEl.focus();
        return;
    }

    HTMLFormElement.prototype.submit.call(formEl);
}