// 입력값 가져오기
function getValue(inputId)
{
    const input = document.getElementById(inputId);
    return input.value.trim();
}
function getValueEl(inputEl) { return (inputEl?.value ?? "").trim(); }

// 입력 확인
function check(inputId, inputName)
{
    const input = document.getElementById(inputId);
    const value = input.value.trim();

    if (value === "")
    {
        showToast(`${inputName}을(를) 입력하세요.`);
        input.focus();
        return false;
    }

    return true;
}
function checkEl(inputEl, inputName)
{
    const value = getValueEl(inputEl);

    if (value === "")
    {
        showToast(`${inputName}을(를) 입력하세요.`);
        inputEl?.focus();
        return false;
    }

    return true;
}

// 길이 제한 체크
function checkMaxLength(inputId, inputName, max)
{
    const input = document.getElementById(inputId);
    const value = input.value.trim();

    if (value.length > max)
    {
        showToast(`${inputName}은(는) ${max}자 이내로 작성하세요.`);
        input.focus();
        return false;
    }

    return true;
}
function checkMaxLengthEl(inputEl, inputName, max)
{
    const value = getValueEl(inputEl);

    if (value.length > max)
    {
        showToast(`${inputName}은(는) ${max}자 이내로 작성하세요.`);
        inputEl?.focus();
        return false;
    }

    return true;
}

// 썸네일 업로드 컨트롤
function imgInputControll()
{
    const imglInput = document.getElementById("thumbnailInput");
    imglInput.click();

    imglInput.onchange = () =>
    {
        const file = imglInput.files[0];
        if (!file) return;
        document.getElementById("imgNameInput").value = file.name;
    };
}