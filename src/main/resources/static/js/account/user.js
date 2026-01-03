// 닉네임 폼 토글
function nameToggle(btn)
{
    const userInfo = btn.closest(".userInfo");
    const nameText = userInfo.querySelectorAll(".title3Text")[1];
    const form = userInfo.querySelector('form[name="nameForm"]');
    const input = form.querySelector('input[name="name"]');

    const isEditing = form.style.display === "flex";

    if (isEditing)
    {
        // 보기 모드로 전환
        form.style.display = "none";
        nameText.style.display = "block";
        btn.textContent = "변경";
    }
    else
    {
        // 수정 모드로 전환
        input.value = nameText.textContent.trim();
        nameText.style.display = "none";
        form.style.display = "flex";
        btn.textContent = "취소";
    }
}

/* 닉네임 변경(추후 구현)
function editName() {  }
*/