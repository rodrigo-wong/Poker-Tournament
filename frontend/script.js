const fetchData = async () => {
  try {
    const response = await fetch("../backend/fetchData.php");
    if (!response.ok) {
      throw new Error("Failed to fetch");
    }
    const results = await response.json();
    console.log(results);
    return results;
  } catch (error) {
    console.error("Error:", error);
  }
};

const filterDataByYears = (data, years) => {
  if (years.includes("all")) return data;
  return data.filter((item) => years.includes(item.year));
};

// Function to create and append rows based on the data
const generateRows = (data) => {

  //Removes previously rendered rows
  const elementsToRemove = document.querySelectorAll(".accordion-item");
  elementsToRemove.forEach((element) => {
    element.remove();
  });

  const container = document.querySelector(".grid-container");
  const filterYear = document.querySelector("#filterYear");

  const selectedYears = Array.from(filterYear.selectedOptions).map(
    (option) => option.value
  );
  console.log(selectedYears);

  var filteredData = filterDataByYears(data, selectedYears);
  console.log(filteredData);

  filteredData.forEach((item, index) => {
    const accordionItem = document.createElement("div");
    accordionItem.classList.add("accordion-item");
    accordionItem.style.gridColumn = 'span 3'
    accordionItem.style.border = 'none';
    accordionItem.style.backgroundColor = 'transparent'
    container.appendChild(accordionItem);
    accordionItem.innerHTML =
      `
      <h2 class="accordion-header fs-6">
        <div class="grid-container-accordion">
            <button class="accordion-button border fs-6" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${index}" aria-expanded="true" aria-controls="collapse${index}">
                ${item.name}
            </button>
            <div class="text-center border header-accordion">${item.points}</div>
            <div class="text-center border header-accordion">${index+1}</div>
        </div>
      </h2>
      <div id="collapse${index}" class="accordion-collapse collapse" data-bs-parent="#accordionExample">
        <div class="accordion-body grid-container-accordion-body text-center" style="margin-top: 0px;">
          <div class="border bg-secondary-subtle">1st</div><div class="border bg-secondary-subtle">2nd</div><div class="border bg-secondary-subtle">3rd</div><div class="border bg-secondary-subtle">4th</div><div class="border bg-secondary-subtle">5th</div><div class="border bg-secondary-subtle">Year</div><div class="border bg-secondary-subtle">Last Updated</div>
          <div>${item.wins}</div><div>${item.second}</div><div>${item.third}</div><div>${item.fourth}</div><div>${item.fifth}</div><div>${item.year}</div><div>${item.last_update}</div>
        </div>
      </div>
      `
  });
  
};
const generateYearOptions = (data) => {
  var years = [];
  data.forEach((item) => {
    if (!years.includes(item.year)) {
      years.push(item.year);
    }
  });

  years.sort(function(a, b) {
    return b - a;
  });

  const filterYear = document.querySelector("#filterYear");

  years.forEach((year) => {
    var option = document.createElement("option");
    option.value = year;
    option.text = year;
    filterYear.appendChild(option);
  });
};

const initialize = async () => {
  var data = await fetchData();
  generateYearOptions(data);
  generateRows(data);
  const filter = document.querySelector("#filterYear");
  filter.addEventListener("change", function () {
    generateRows(data);
  });
};

initialize();
