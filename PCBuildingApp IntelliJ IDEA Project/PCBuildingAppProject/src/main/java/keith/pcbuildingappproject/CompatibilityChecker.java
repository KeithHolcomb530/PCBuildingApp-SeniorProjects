/*
    Name: Keith Holcomb
    Date: May 5th, 2025
    Course: CISS:451 Senior Projects 001
    Description: CompatibilityChecker is the class that handles all the PC part compatibiltiy checks within the PC Building App.
        Code comments accidentally not created for this section. This was an accidental oversight of code documentation.
*/
package keith.pcbuildingappproject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

public class CompatibilityChecker {
    
    private PCpart getPartByType(List<PCpart> parts, String partType) {
        for (PCpart part : parts) {
            if (partType.equalsIgnoreCase(part.getPartType())) {
                return part;
            }
        }
        return null;
    }

    private Map<String, Object> getSpecifications(PCpart part) {
        return part != null ? part.getSpecifications() : null;
    }

    public void addCompatibilityError(Map<String, List<String>> errorMessages, String partType, String errorMessage) {
        errorMessages.computeIfAbsent(partType, k -> new ArrayList<>()).add(errorMessage);
    }

    private Integer extractAndParseInt(Map<String, Object> specs, String key, String regex, Map<String, List<String>> errorMessages, String partType) {
        if (specs != null && specs.containsKey(key)) {
            Object value = specs.get(key);
            if (value != null) {
                String stringValue;
                if (value instanceof Integer) {
                    stringValue = value.toString();
                } else if (value instanceof String) {
                    stringValue = (String) value;
                } else {
                    addCompatibilityError(errorMessages, partType, "Unexpected data type for " + key + ".");
                    return null;
                }
                try {
                    String cleanedValue = stringValue.replaceAll(regex, "").trim();
                    if (!cleanedValue.isEmpty()) {
                        return Integer.parseInt(cleanedValue);
                    }
                } catch (NumberFormatException e) {
                    addCompatibilityError(errorMessages, partType, "Invalid numeric format for " + key + ".");
                    return null;
                } catch (PatternSyntaxException e) {
                    addCompatibilityError(errorMessages, partType, "Invalid regex pattern for " + key + ".");
                    return null;
                }
            }
        }
        return null;
    }

    public void cpuCompatibility(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        PCpart cpu = getPartByType(parts, "cpu");
        PCpart motherboard = getPartByType(parts, "motherboard");
        PCpart cpuCooler = getPartByType(parts, "cpu cooler");

        if (cpu != null && motherboard != null) {
            checkCpuMotherboardCompatibility(cpu, motherboard, errorMessages);
        }

        if (cpu != null && cpuCooler != null) {
            checkCpuCoolerCompatibility(cpu, cpuCooler, errorMessages);
        }
    }

    private void checkCpuMotherboardCompatibility(PCpart cpu, PCpart motherboard, Map<String, List<String>> errorMessages) {
        Map<String, Object> cpuSpecs = getSpecifications(cpu);
        Map<String, Object> motherboardSpecs = getSpecifications(motherboard);

        if (cpuSpecs != null && motherboardSpecs != null) {
            String cpuSocket = (String) cpuSpecs.getOrDefault("socket", "");
            String motherboardSocket = (String) motherboardSpecs.getOrDefault("socket", "");
            if (!cpuSocket.equals(motherboardSocket)) {
                addCompatibilityError(errorMessages, "CPU", "CPU socket does not match Motherboard socket.");
                addCompatibilityError(errorMessages, "Motherboard", "Motherboard socket does not match CPU socket.");
            }

            Integer cpuMaxMemorySpeed = extractAndParseInt(cpuSpecs, "max_memory_speed", "[^\\d]", errorMessages, "CPU");
            Integer motherboardMemorySpeedSupport = extractAndParseInt(motherboardSpecs, "memory_speed_support", "[^\\d]", errorMessages, "Motherboard");

            if (cpuMaxMemorySpeed != null && motherboardMemorySpeedSupport != null && cpuMaxMemorySpeed > motherboardMemorySpeedSupport) {
                addCompatibilityError(errorMessages, "CPU", "CPU max memory speed exceeds Motherboard support.");
                addCompatibilityError(errorMessages, "Motherboard", "Motherboard does not support CPU max memory speed.");
            }
        }
    }

    private void checkCpuCoolerCompatibility(PCpart cpu, PCpart cpuCooler, Map<String, List<String>> errorMessages) {
        Map<String, Object> cpuSpecs = getSpecifications(cpu);
        Map<String, Object> cpuCoolerSpecs = getSpecifications(cpuCooler);

        if (cpuSpecs != null && cpuCoolerSpecs != null) {
            String cpuSocket = (String) cpuSpecs.getOrDefault("socket", "");
            String coolerSockets = (String) cpuCoolerSpecs.getOrDefault("socket_compatibility", "");

            if (coolerSockets != null && !coolerSockets.toLowerCase().contains(cpuSocket.toLowerCase())) {
                addCompatibilityError(errorMessages, "CPU", "CPU socket not compatible with CPU cooler.");
                addCompatibilityError(errorMessages, "CPU Cooler", "CPU Cooler not compatible with CPU socket.");
            }
        }
    }

    public void motherboardCompatibility(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        PCpart motherboard = getPartByType(parts, "motherboard");
        PCpart ram = getPartByType(parts, "ram");
        PCpart gpu = getPartByType(parts, "gpu");
        PCpart pcCase = getPartByType(parts, "case");
        if (motherboard != null) {
            checkMotherboardRamCompatibility(motherboard, ram, errorMessages);
            checkMotherboardCaseCompatibility(motherboard, pcCase, errorMessages);
            if (gpu != null) {
                checkMotherboardGpuCompatibility(motherboard, gpu, errorMessages);
            }
        }
    }

    private void checkMotherboardRamCompatibility(PCpart motherboard, PCpart ram, Map<String, List<String>> errorMessages) {
        if (ram != null) {
            Map<String, Object> motherboardSpecs = getSpecifications(motherboard);
            Map<String, Object> ramSpecs = getSpecifications(ram);

            if (motherboardSpecs != null && ramSpecs != null) {
                String ramType = (String) ramSpecs.getOrDefault("type", "");
                String motherboardMemoryType = (String) motherboardSpecs.getOrDefault("memory_type", "");

                if (motherboardMemoryType != null && !motherboardMemoryType.toLowerCase().contains(ramType.toLowerCase())) {
                    addCompatibilityError(errorMessages, "RAM", "RAM type is not compatible with Motherboard.");
                    addCompatibilityError(errorMessages, "Motherboard", "Motherboard does not support this RAM type.");
                }

                Integer ramSpeed = extractAndParseInt(ramSpecs, "speed", "[^\\d]", errorMessages, "RAM");
                Integer motherboardMemorySpeedSupport = extractAndParseInt(motherboardSpecs, "memory_speed_support", "[^\\d]", errorMessages, "Motherboard");

                if (ramSpeed != null && motherboardMemorySpeedSupport != null && motherboardMemorySpeedSupport < ramSpeed) {
                    addCompatibilityError(errorMessages, "RAM", "RAM speed is not supported by motherboard.");
                    addCompatibilityError(errorMessages, "Motherboard", "Motherboard does not support this RAM speed.");
                }
            }
        }
    }

    private void checkMotherboardGpuCompatibility(PCpart motherboard, PCpart gpu, Map<String, List<String>> errorMessages) {
        if (gpu != null) {
            Map<String, Object> motherboardSpecs = getSpecifications(motherboard);
            Map<String, Object> gpuSpecs = getSpecifications(gpu);

            if (motherboardSpecs != null && gpuSpecs != null) {
                String gpuInterface = (String) gpuSpecs.getOrDefault("interface", "");
                String motherboardPcieSlots = (String) motherboardSpecs.getOrDefault("pcie_slots", "");

                if (motherboardPcieSlots != null && gpuInterface != null && !motherboardPcieSlots.toLowerCase().contains(gpuInterface.toLowerCase().split(" ")[0])) {
                    addCompatibilityError(errorMessages, "GPU", "GPU interface is not compatible with motherboard PCIe slots.");
                    addCompatibilityError(errorMessages, "Motherboard", "Motherboard PCIe slots do not support GPU interface.");
                }
            }
        }
    }

    private void checkMotherboardCaseCompatibility(PCpart motherboard, PCpart pcCase, Map<String, List<String>> errorMessages) {
        if (pcCase != null) {
            Map<String, Object> motherboardSpecs = getSpecifications(motherboard);
            Map<String, Object> caseSpecs = getSpecifications(pcCase);

            if (motherboardSpecs != null && caseSpecs != null) {
                String motherboardFormFactor = (String) motherboardSpecs.getOrDefault("form_factor", "");
                String caseMotherboardSupport = (String) caseSpecs.getOrDefault("motherboard_form_factor", "");

                if (caseMotherboardSupport != null && motherboardFormFactor != null && !caseMotherboardSupport.toLowerCase().contains(motherboardFormFactor.toLowerCase())) {
                    addCompatibilityError(errorMessages, "Motherboard", "Motherboard form factor is not supported by the case.");
                    addCompatibilityError(errorMessages, "Case", "Case does not support Motherboard form factor.");
                }
            }
        }
    }

    public void cpuCoolerCompatibility(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        PCpart cpuCooler = getPartByType(parts, "cpu cooler");
        PCpart pcCase = getPartByType(parts, "case");

        if (cpuCooler != null) {
            checkCpuCoolerCaseCompatibility(cpuCooler, pcCase, errorMessages);
        }
    }

    private void checkCpuCoolerCaseCompatibility(PCpart cpuCooler, PCpart pcCase, Map<String, List<String>> errorMessages) {
        if (pcCase != null) {
            Map<String, Object> coolerSpecs = getSpecifications(cpuCooler);
            Map<String, Object> caseSpecs = getSpecifications(pcCase);

            if (coolerSpecs != null && caseSpecs != null) {
                checkCpuCoolerHeightCompatibility(coolerSpecs, caseSpecs, errorMessages);
                checkCpuCoolerRadiatorCompatibility(coolerSpecs, caseSpecs, errorMessages);
            }
        }
    }

    private void checkCpuCoolerHeightCompatibility(Map<String, Object> coolerSpecs, Map<String, Object> caseSpecs, Map<String, List<String>> errorMessages) {
        if (coolerSpecs != null && caseSpecs != null) {
            Integer coolerHeight = extractAndParseInt(coolerSpecs, "cooler_height", "mm", errorMessages, "CPU Cooler");
            Integer caseMaxCoolerHeight = extractAndParseInt(caseSpecs, "max_cpu_cooler_height", "mm", errorMessages, "Case");

            if (coolerHeight != null && caseMaxCoolerHeight != null && coolerHeight > caseMaxCoolerHeight) {
                addCompatibilityError(errorMessages, "CPU Cooler", "CPU Cooler height exceeds Case maximum CPU Cooler height.");
                addCompatibilityError(errorMessages, "Case", "Case cannot fit selected CPU Cooler due to height.");
            }
        }
    }

    private void checkCpuCoolerRadiatorCompatibility(Map<String, Object> coolerSpecs, Map<String, Object> caseSpecs, Map<String, List<String>> errorMessages) {
        if (coolerSpecs != null && caseSpecs != null) {
            String coolerRadiatorSize = (String) coolerSpecs.getOrDefault("radiator_size", "");
            String caseRadiatorSupport = (String) caseSpecs.getOrDefault("radiator_support", "");

            if (coolerRadiatorSize != null && caseRadiatorSupport != null && !caseRadiatorSupport.toLowerCase().contains(coolerRadiatorSize.toLowerCase())) {
                addCompatibilityError(errorMessages, "CPU Cooler", "CPU Cooler radiator size not supported by selected Case.");
                addCompatibilityError(errorMessages, "Case", "Case does not support CPU Cooler radiator size.");
            }
        }
    }

    public void ramCompatibility(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        PCpart motherboard = getPartByType(parts, "motherboard");
        List<PCpart> ramModules = getRamModules(parts);

        if (motherboard != null && !ramModules.isEmpty()) {
            checkRamSlotsCompatibility(motherboard, ramModules, errorMessages);
            checkRamCapacityCompatibility(motherboard, ramModules, errorMessages);
            checkRamSpeedCompatibility(motherboard, ramModules, errorMessages);
        }
    }

    private List<PCpart> getRamModules(List<PCpart> parts) {
        List<PCpart> ramModules = new ArrayList<>();
        for (PCpart part : parts) {
            if ("ram".equalsIgnoreCase(part.getPartType())) {
                ramModules.add(part);
            }
        }
        return ramModules;
    }

    private void checkRamSlotsCompatibility(PCpart motherboard, List<PCpart> ramModules, Map<String, List<String>> errorMessages) {
        Map<String, Object> motherboardSpecs = getSpecifications(motherboard);
        if (motherboardSpecs != null) {
            int motherboardRamSlots = (int) motherboardSpecs.getOrDefault("memory_slots", 0);
            if (ramModules.size() > motherboardRamSlots) {
                addCompatibilityError(errorMessages, "RAM", "Number of RAM modules exceeds Motherboard RAM slots.");
                addCompatibilityError(errorMessages, "Motherboard", "Motherboard has insufficient RAM slots.");
            }
        }
    }

    private void checkRamCapacityCompatibility(PCpart motherboard, List<PCpart> ramModules, Map<String, List<String>> errorMessages) {
        Map<String, Object> motherboardSpecs = getSpecifications(motherboard);
        if (motherboardSpecs != null) {
            String motherboardMaxMemoryStr = (String) motherboardSpecs.getOrDefault("max_memory", "0GB");
            int motherboardMaxMemory = Integer.parseInt(motherboardMaxMemoryStr.replaceAll("GB", "").trim());
            int totalRamCapacity = 0;
            for (PCpart ramModule : ramModules) {
                Map<String, Object> ramSpecs = getSpecifications(ramModule);
                if (ramSpecs != null) {
                    String ramCapacityStr = (String) ramSpecs.getOrDefault("capacity", "0GB");
                    totalRamCapacity += Integer.parseInt(ramCapacityStr.replaceAll("GB", "").trim());
                }
            }
            if (totalRamCapacity > motherboardMaxMemory) {
                addCompatibilityError(errorMessages, "RAM", "Total RAM capacity exceeds Motherboard maximum memory.");
                addCompatibilityError(errorMessages, "Motherboard", "Motherboard has insufficient maximum memory.");
            }
        }
    }

    private void checkRamSpeedCompatibility(PCpart motherboard, List<PCpart> ramModules, Map<String, List<String>> errorMessages) {
        Map<String, Object> motherboardSpecs = getSpecifications(motherboard);
        if (motherboardSpecs != null) {
            String motherboardMemorySpeedSupportStr = (String) motherboardSpecs.getOrDefault("memory_speed_support", "");
            if (motherboardMemorySpeedSupportStr != null) {
                try {
                    int motherboardMemorySpeedSupport = Integer.parseInt(motherboardMemorySpeedSupportStr);
                    for (PCpart ramModule : ramModules) {
                        Map<String, Object> ramSpecs = getSpecifications(ramModule);
                        if (ramSpecs != null) {
                            String ramSpeedStr = (String) ramSpecs.getOrDefault("speed", "");
                            if (ramSpeedStr != null) {
                                int ramSpeed = Integer.parseInt(ramSpeedStr);
                                if (ramSpeed > motherboardMemorySpeedSupport) {
                                    addCompatibilityError(errorMessages, "RAM", "RAM speed exceeds motherboard support.");
                                    addCompatibilityError(errorMessages, "Motherboard", "Motherboard does not support RAM speed.");
                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    return;
                }
            }
        }
    }

    public void gpuCompatibility(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        PCpart gpu = getPartByType(parts, "gpu");
        PCpart pcCase = getPartByType(parts, "case");

        if (gpu != null) {
            checkGpuCaseCompatibility(gpu, pcCase, errorMessages);
        }
    }

    private void checkGpuCaseCompatibility(PCpart gpu, PCpart pcCase, Map<String, List<String>> errorMessages) {
        if (pcCase != null) {
            Map<String, Object> gpuSpecs = getSpecifications(gpu);
            Map<String, Object> caseSpecs = getSpecifications(pcCase);

            if (gpuSpecs != null && caseSpecs != null) {
                checkGpuLengthCompatibility(gpuSpecs, caseSpecs, errorMessages);
                checkGpuWidthCompatibility(gpuSpecs, caseSpecs, errorMessages);
            }
        }
    }

    private void checkGpuLengthCompatibility(Map<String, Object> gpuSpecs, Map<String, Object> caseSpecs, Map<String, List<String>> errorMessages) {
        Integer gpuLength = extractAndParseInt(gpuSpecs, "gpu_length", "mm", errorMessages, "GPU");
        Integer caseMaxLength = extractAndParseInt(caseSpecs, "max_gpu_length", "mm", errorMessages, "Case");
        if (gpuLength != null && caseMaxLength != null && gpuLength > caseMaxLength) {
            addCompatibilityError(errorMessages, "GPU", "GPU length exceeds Case maximum GPU length.");
            addCompatibilityError(errorMessages, "Case", "Case cannot fit selected GPU due to length.");
        }
    }

    private void checkGpuWidthCompatibility(Map<String, Object> gpuSpecs, Map<String, Object> caseSpecs, Map<String, List<String>> errorMessages) {
        if (gpuSpecs != null && caseSpecs != null) {
            Integer caseMaxWidth = extractAndParseInt(caseSpecs, "max_gpu_width", "[^\\d.]", errorMessages, "Case");
            if (caseMaxWidth != null) {
                String gpuWidthStr = (String) gpuSpecs.getOrDefault("gpu_width", null);
                if (gpuWidthStr != null) {
                    try {
                        double gpuWidth;
                        if (gpuWidthStr.contains("slots")) {
                            String gpuWidthStrCleaned = gpuWidthStr.replaceAll("slots", "").trim();
                            gpuWidth = Double.parseDouble(gpuWidthStrCleaned) * 21.1;
                        } else if (gpuWidthStr.contains("mm")) {
                            String gpuWidthStrCleaned = gpuWidthStr.replaceAll("mm", "").trim();
                            gpuWidth = Double.parseDouble(gpuWidthStrCleaned);
                        }
                        else {
                            gpuWidth = Double.parseDouble(gpuWidthStr);
                        }
                        if (gpuWidth > caseMaxWidth) {
                            addCompatibilityError(errorMessages, "GPU", "GPU width may not fit within the selected Case.");
                            addCompatibilityError(errorMessages, "Case", "Case may not fit the selected GPU due to width.");
                        }
                    } catch (NumberFormatException e) {
                        addCompatibilityError(errorMessages, "GPU", "Invalid GPU width format.");
                    }
                }
            }
        }
    }

    public void psuCompatibility(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        PCpart psu = getPartByType(parts, "power supply");
        List<PCpart> gpus = getPartsByType(parts, "gpu");
        PCpart motherboard = getPartByType(parts, "motherboard");
        if (psu != null) {
            checkPsuMotherboardCompatibility(psu, motherboard, errorMessages);
            checkPsuWattageCompatibility(psu, parts, errorMessages);
            if (gpus != null) {
                for (PCpart gpu : gpus) {
                    checkPsuGpuCompatibility(psu, gpu, errorMessages);
                }
            }
        }
    }

    private void checkPsuGpuCompatibility(PCpart psu, PCpart gpu, Map<String, List<String>> errorMessages) {
        if (psu != null && gpu != null) {
            Map<String, Object> psuSpecs = getSpecifications(psu);
            Map<String, Object> gpuSpecs = getSpecifications(gpu);
            if (psuSpecs != null && gpuSpecs != null) {
                String gpuPowerConnectorsStr = (String) gpuSpecs.getOrDefault("power_connectors", "0");
                String psuPcieConnectorsStr = (String) psuSpecs.getOrDefault("pcie_connectors", "0");
                int gpuPowerConnectors = 0;
                int psuPcieConnectors = 0;
                if (gpuPowerConnectorsStr != null && !gpuPowerConnectorsStr.isEmpty()) {
                    gpuPowerConnectors = Integer.parseInt(gpuPowerConnectorsStr.replaceAll("[^\\d]", ""));
                }
                if (psuPcieConnectorsStr != null && !psuPcieConnectorsStr.isEmpty()) {
                    psuPcieConnectors = Integer.parseInt(psuPcieConnectorsStr.replaceAll("[^\\d]", ""));
                }
                if (gpuPowerConnectors > psuPcieConnectors) {
                    addCompatibilityError(errorMessages, "GPU", "PSU does not have enough power connectors for the GPU.");
                    addCompatibilityError(errorMessages, "Power Supply", "PSU lacks necessary GPU power connectors.");
                }
            }
        }
    }

    private List<PCpart> getPartsByType(List<PCpart> parts, String partType) {
        List<PCpart> partsOfType = new ArrayList<>();
        for (PCpart part : parts) {
            if (partType.equalsIgnoreCase(part.getPartType())) {
                partsOfType.add(part);
            }
        }
        return partsOfType;
    }

    private void checkPsuMotherboardCompatibility(PCpart psu, PCpart motherboard, Map<String, List<String>> errorMessages) {
        if (motherboard != null) {
            Map<String, Object> psuSpecs = getSpecifications(psu);
            Map<String, Object> motherboardSpecs = getSpecifications(motherboard);
            if (psuSpecs != null && motherboardSpecs != null) {
                String psuMotherboardConnectors = (String) psuSpecs.getOrDefault("motherboard_connectors", "");
                String motherboardPowerConnectors = (String) motherboardSpecs.getOrDefault("power_connectors", "");
                if (psuMotherboardConnectors != null && motherboardPowerConnectors != null && !psuMotherboardConnectors.toLowerCase().contains(motherboardPowerConnectors.toLowerCase())) {
                    addCompatibilityError(errorMessages, "Motherboard", "Motherboard power connectors are not compatible with PSU.");
                    addCompatibilityError(errorMessages, "Power Supply", "PSU does not have the required power connectors for the Motherboard.");
                }
            }
        }
    }

    private void checkPsuWattageCompatibility(PCpart psu, List<PCpart> parts, Map<String, List<String>> errorMessages) {
        Map<String, Object> psuSpecs = getSpecifications(psu);
        if (psuSpecs != null) {
            Integer psuWattage = extractAndParseInt(psuSpecs, "wattage", "W", errorMessages, "Power Supply");
            if (psuWattage != null) {
                int totalWattage = calculateTotalWattage(parts, errorMessages);
                if (totalWattage > psuWattage) {
                    addCompatibilityError(errorMessages, "Power Supply", "PSU wattage is insufficient for the system.");
                }
            }
        }
    }

    private int calculateTotalWattage(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        int totalWattage = 0;
        for (PCpart part : parts) {
            Map<String, Object> partSpecs = getSpecifications(part);
            if (partSpecs != null) {
                Integer partWattage = extractAndParseInt(partSpecs, "wattage", "W", errorMessages, part.getPartType());
                if (partWattage != null) {
                    totalWattage += partWattage;
                }
            }
        }
        return totalWattage;
    }

    public void caseCompatibility(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        PCpart pcCase = getPartByType(parts, "case");
        PCpart motherboard = getPartByType(parts, "motherboard");
        List<PCpart> storageDevices = getPartsByType(parts, "storage");
        if (pcCase != null) {
            checkCaseMotherboardCompatibility(pcCase, motherboard, errorMessages);
            checkCaseStorageCompatibility(pcCase, storageDevices, errorMessages);
        }
    }

    private void checkCaseMotherboardCompatibility(PCpart pcCase, PCpart motherboard, Map<String, List<String>> errorMessages) {
        if (motherboard != null) {
            Map<String, Object> caseSpecs = getSpecifications(pcCase);
            Map<String, Object> motherboardSpecs = getSpecifications(motherboard);
            if (caseSpecs != null && motherboardSpecs != null) {
                String motherboardFormFactor = (String) motherboardSpecs.getOrDefault("form_factor", "");
                String caseMotherboardSupport = (String) caseSpecs.getOrDefault("motherboard_form_factor", "");
                if (caseMotherboardSupport != null && motherboardFormFactor != null && !caseMotherboardSupport.toLowerCase().contains(motherboardFormFactor.toLowerCase())) {
                    addCompatibilityError(errorMessages, "Motherboard", "Motherboard form factor is not supported by the case.");
                    addCompatibilityError(errorMessages, "Case", "Case does not support motherboard form factor.");
                }
            }
        }
    }

    private void checkCaseStorageCompatibility(PCpart pcCase, List<PCpart> storageDevices, Map<String, List<String>> errorMessages) {
        if (!storageDevices.isEmpty()) {
            Map<String, Object> caseSpecs = getSpecifications(pcCase);
            if (caseSpecs != null) {
                int caseHddCount = 0;
                int caseSsdCount = 0;
                String caseHddBays = (String) caseSpecs.getOrDefault("internal_3.5\"_bays", "0");
                String caseSsdBays = (String) caseSpecs.getOrDefault("internal_2.5\"_bays", "0");
                if (caseHddBays != null) {
                    caseHddCount = Integer.parseInt(caseHddBays.replaceAll("[^\\d]", ""));
                }
                if (caseSsdBays != null) {
                    caseSsdCount = Integer.parseInt(caseSsdBays.replaceAll("[^\\d]", ""));
                }
                int hddCount = 0;
                int ssdCount = 0;
                for (PCpart storageDevice : storageDevices) {
                    Map<String, Object> storageSpecs = getSpecifications(storageDevice);
                    if (storageSpecs != null) {
                        String formFactor = (String) storageSpecs.getOrDefault("form_factor", "");
                        if (formFactor != null) {
                            if (formFactor.contains("3.5")) {
                                hddCount++;
                            } else if (formFactor.contains("2.5")) {
                                ssdCount++;
                            }
                        }
                    }
                }
                if (hddCount > caseHddCount) {
                    addCompatibilityError(errorMessages, "Storage", "Too many 3.5 inch drives for this Case.");
                    addCompatibilityError(errorMessages, "Case", "Case has insufficient 3.5 inch drive bays.");
                }
                if (ssdCount > caseSsdCount) {
                    addCompatibilityError(errorMessages, "Storage", "Too many 2.5 inch drives for this Case.");
                    addCompatibilityError(errorMessages, "Case", "Case has insufficient 2.5 inch drive bays.");
                }
            }
        }
    }

    public void caseFanCompatibility(List<PCpart> parts, Map<String, List<String>> errorMessages) {
        PCpart pcCase = getPartByType(parts, "case");
        List<PCpart> caseFans = getPartsByType(parts, "case fan");
        if (pcCase != null && !caseFans.isEmpty()) {
            checkCaseFanMountCompatibility(pcCase, caseFans, errorMessages);
        }
    }

    private void checkCaseFanMountCompatibility(PCpart pcCase, List<PCpart> caseFans, Map<String, List<String>> errorMessages) {
        Map<String, Object> caseSpecs = getSpecifications(pcCase);
        if (caseSpecs != null) {
            Map<String, Map<String, Integer>> caseFanMounts = getCaseFanMounts(caseSpecs, errorMessages);
            for (PCpart fan : caseFans) {
                Map<String, Object> fanSpecs = getSpecifications(fan);
                if (fanSpecs != null) {
                    checkFanCompatibilityWithMounts(fanSpecs, caseFanMounts, errorMessages);
                }
            }
        }
    }

    private Map<String, Map<String, Integer>> getCaseFanMounts(Map<String, Object> caseSpecs, Map<String, List<String>> errorMessages) {
        Map<String, Map<String, Integer>> fanMounts = new HashMap<>();
        for (Map.Entry<String, Object> entry : caseSpecs.entrySet()) {
            if (entry.getKey().contains("fan_support")) {
                String mountSizes = (String) entry.getValue();
                if (mountSizes != null) {
                    String[] mounts = mountSizes.split("/");
                    Map<String, Integer> sizeCounts = new HashMap<>();
                    for (String mount : mounts) {
                        String cleanedMount = mount.trim().toLowerCase();
                        String[] parts = cleanedMount.split("x");
                        if (parts.length == 2) {
                            try {
                                int count = Integer.parseInt(parts[0]);
                                String size = parts[1].trim();
                                sizeCounts.put(size, count);
                            } catch (NumberFormatException e) {
                                addCompatibilityError(errorMessages, "Case", "Error parsing mount: " + mount);
                            }
                        } else {
                            addCompatibilityError(errorMessages, "Case", "Invalid mount format: " + mount);
                        }
                    }
                    fanMounts.put(entry.getKey(), sizeCounts);
                }
            }
        }
        return fanMounts;
    }

    private void checkFanCompatibilityWithMounts(Map<String, Object> fanSpecs, Map<String, Map<String, Integer>> caseFanMounts, Map<String, List<String>> errorMessages) {
        String fanSize = (String) fanSpecs.get("size");
        if (fanSize != null) {
            fanSize = fanSize.trim().toLowerCase();
            boolean compatible = false;
            for (Map.Entry<String, Map<String, Integer>> mountOptions : caseFanMounts.entrySet()) {
                if (mountOptions.getValue().containsKey(fanSize)) {
                    compatible = true;
                    break;
                }
            }
            if (!compatible) {
                addCompatibilityError(errorMessages, "Case Fan", "Fan size (" + fanSize + ") is not supported by the selected Case.");
                addCompatibilityError(errorMessages, "Case", "Selected Case does not support fan size (" + fanSize + ").");
            }
        }
    }
}
